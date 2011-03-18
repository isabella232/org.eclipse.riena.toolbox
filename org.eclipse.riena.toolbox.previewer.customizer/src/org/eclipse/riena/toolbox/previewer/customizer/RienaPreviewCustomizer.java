package org.eclipse.riena.toolbox.previewer.customizer;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import org.eclipse.riena.toolbox.previewer.IPreviewCustomizer;
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
			setLnf(classLoader, lnf);
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
	 * @param classLoader
	 */
	private void setLnf(final ClassLoader classLoader, final String lnfName) {
		try {
			LnFUpdater.getInstance().clearCache();
			final Class<?> loadClass = classLoader.loadClass(lnfName);
			final RienaDefaultLnf lnf = (RienaDefaultLnf) loadClass.newInstance();

			final Class<?> lnfTheme = classLoader.loadClass(lnf.getTheme().getClass().getName());
			final RienaDefaultTheme theme = (RienaDefaultTheme) lnfTheme.newInstance();
			lnf.setTheme(theme);

			LnfManager.setLnf(lnf);
			return;
		} catch (final ClassNotFoundException e) {
			showWarning("ClassNotFoundException", e); //$NON-NLS-1$
		} catch (final InstantiationException e) {
			showWarning("InstantiationException", e); //$NON-NLS-1$
		} catch (final IllegalAccessException e) {
			showWarning("IllegalAccessException", e); //$NON-NLS-1$
		}
	}

	private void showWarning(final String message, final Exception e) {
		final String logMessage = message + "\n" + e.getMessage(); //$NON-NLS-1$
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Warning",
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
				System.out.println("setTooltip " + bindingProperty);
			}

			@Override
			public void handleControl(final Control control) {
				control.redraw();
			}
		};
		finder.run();

	}
}
