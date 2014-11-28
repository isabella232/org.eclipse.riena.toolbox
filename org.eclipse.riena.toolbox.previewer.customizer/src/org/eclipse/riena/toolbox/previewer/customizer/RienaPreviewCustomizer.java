/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.toolbox.previewer.customizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import org.eclipse.riena.toolbox.previewer.IPreviewCustomizer;
import org.eclipse.riena.toolbox.previewer.WorkspaceClassLoader;
import org.eclipse.riena.toolbox.previewer.customizer.preferences.PreferenceConstants;
import org.eclipse.riena.ui.swt.lnf.LnFUpdater;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultTheme;
import org.eclipse.riena.ui.swt.utils.SWTControlFinder;

public class RienaPreviewCustomizer implements IPreviewCustomizer {

	/**
	 * @param info
	 */
	private void updateLnf(final ClassLoader classLoader) {
		final String lnf = getLnfFromPreferences();
		if (null != lnf) {
			setLookAndFeel(classLoader, lnf);
		}
	}

	/**
	 * @return
	 */
	private String getLnfFromPreferences() {
		final String customLnfClass = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.LNF_CLASS_NAME);

		String lnf = Activator.getDefault().getPreferenceStore().getDefaultString(PreferenceConstants.LNF_CLASS_NAME);
		if (null != customLnfClass && customLnfClass.trim().length() > 0) {
			lnf = customLnfClass;
		}
		return lnf;
	}

	/**
	 * Sets the given Look and Feel for the view of the preview.
	 * <p>
	 * First this method tries to load the LnF with the given class loader. If
	 * that isn't possible the LnF will be searched in every project of the
	 * workspace.
	 * </p>
	 * 
	 * @param classLoader
	 *            the class loader of LnF
	 * @param lnfName
	 *            name of the Look and Feel
	 */
	private void setLookAndFeel(final ClassLoader classLoader, final String lnfName) {

		Exception firstException;

		try {
			setLnf(classLoader, lnfName);
			return;

			// if an exception happens, do nothing
			// try other class loader
		} catch (final ClassNotFoundException ex) {
			firstException = ex;
		} catch (final InstantiationException ex) {
			firstException = ex;
		} catch (final IllegalAccessException ex) {
			firstException = ex;
		}

		try {
			final ClassLoader otherClassLoader = findAlternateProjectForLnf(lnfName);
			if (otherClassLoader != null) {
				setLnf(otherClassLoader, lnfName);
			} else {
				// no other class loader
				// throw the exception of the first try with the given class loader
				throw firstException;
			}
		} catch (final JavaModelException ex) {
			showWarning(ex.getClass().getSimpleName(), ex);
		} catch (final CoreException ex) {
			showWarning(ex.getClass().getSimpleName(), ex);
		} catch (final ClassNotFoundException ex) {
			showWarning(ex.getClass().getSimpleName(), ex);
		} catch (final InstantiationException ex) {
			showWarning(ex.getClass().getSimpleName(), ex);
		} catch (final IllegalAccessException ex) {
			showWarning(ex.getClass().getSimpleName(), ex);
		} catch (final Exception ex) {
			showWarning(ex.getClass().getSimpleName(), ex);
		}

	}

	/**
	 * Returns the class loader of the project with the Look and Feel or
	 * {@code null} if no project was found.
	 * 
	 * @param lnfName
	 *            name of the Look and Feel
	 * @return class loader of the project with the LnF
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private ClassLoader findAlternateProjectForLnf(final String lnfName) throws JavaModelException, CoreException {

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject[] projects = workspace.getRoot().getProjects();
		for (final IProject project : projects) {
			// is project open and is this a java project?
			if (project.isAccessible() && project.hasNature(JavaCore.NATURE_ID)) {
				final IJavaProject javaProject = JavaCore.create(project);
				// is the given Look and Feel part of this java project?
				final IType type = javaProject.findType(lnfName);
				if (type != null) {
					final ClassLoader parentClassLoader = getParentClass().getClassLoader();
					return WorkspaceClassLoader.createClassloader(parentClassLoader, javaProject);
				}
			}
		}
		return null;

	}

	/**
	 * Sets the given Look and Feel for the view of the preview.
	 * 
	 * @param classLoader
	 *            the class loader that <i>should be able</i> to load and
	 *            instantiate the given Look and Feel.
	 * @param lnfName
	 *            name of the Look and Feel
	 * @throws ClassNotFoundException
	 *             class of the LnF wasn't found
	 * @throws InstantiationException
	 *             creation of LnF failed
	 * @throws IllegalAccessException
	 *             creation of LnF failed
	 */
	private void setLnf(final ClassLoader classLoader, final String lnfName) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		LnFUpdater.getInstance().clearCache();

		// create Look and Feel instance
		final Class<?> loadClass = classLoader.loadClass(lnfName);
		final RienaDefaultLnf lnf = (RienaDefaultLnf) loadClass.newInstance();

		// set name of the theme
		final String themeName = lnf.getTheme().getClass().getName();
		final Class<?> lnfTheme = classLoader.loadClass(themeName);
		final RienaDefaultTheme theme = (RienaDefaultTheme) lnfTheme.newInstance();
		lnf.setTheme(theme);

		// set the Look and Feel 
		LnfManager.setLnf(lnf);

	}

	private void showWarning(final String message, final Exception e) {
		final String logMessage = message + "\n" + e.getMessage(); //$NON-NLS-1$
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Warning", //$NON-NLS-1$
				logMessage);
		Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, logMessage, e));
	}

	public void beforeClassLoad(final ClassLoader project) {
		updateLnf(project);
	}

	public Class<?> getParentClass() {
		return RienaDefaultLnf.class;
	}

	public void afterCreation(final Composite parent) {
		final String currentLnf = getLnfFromPreferences();
		if (null == currentLnf) {
			// reset the view to its original state
			final SWTControlFinder finder = new SWTControlFinder(parent) {
				@Override
				public void handleControl(final Control control) {
					super.handleControl(control);
					control.redraw();
				}

				@Override
				public void handleBoundControl(final Control control, final String bindingProperty) {
				}
			};
			finder.run();
		}

		if (!Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SHOW_RIDGET_IDS)) {
			return;
		}

		final SWTControlFinder finder = new SWTControlFinder(parent) {
			@Override
			public void handleBoundControl(final Control control, final String bindingProperty) {
				control.setToolTipText(String.format("Ridget-Id '%s'", bindingProperty)); //$NON-NLS-1$
				control.redraw();
			}
		};
		finder.run();

	}
}
