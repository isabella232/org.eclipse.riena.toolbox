/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.toolbox.assemblyeditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.views.AssemblyView;

public class StartupEditorListener implements IStartup {

	public void earlyStartup() {
		IWorkbenchPage activePage = null;
		final IPartListener listener = new ActiveEditorPartListener();

		IWorkbenchWindow[] windows = null;
		IWorkbenchPart activePart = null;

		windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			if (windows[i] != null) {
				activePage = windows[i].getActivePage();
				if (activePage != null) {
					activePage.addPartListener(listener);
					activePart = activePage.getActivePart();
					if (activePart != null) {
						listener.partOpened(activePart);
						listener.partActivated(activePart);
					}
				}
			}
		}
	}

	private static class ActiveEditorPartListener implements IPartListener {
		public void partBroughtToTop(final IWorkbenchPart part) {
		}

		public void partClosed(final IWorkbenchPart part) {
		}

		public void partDeactivated(final IWorkbenchPart part) {
		}

		public void partActivated(final IWorkbenchPart part) {
			selectCorrespondingNodeInAssemblyTree(part);
		}

		public void partOpened(final IWorkbenchPart part) {
			selectCorrespondingNodeInAssemblyTree(part);
		}

		private void selectCorrespondingNodeInAssemblyTree(final IWorkbenchPart part) {
			if (part instanceof CompilationUnitEditor) {
				final CompilationUnitEditor edi = (CompilationUnitEditor) part;
				final FileEditorInput inp = (FileEditorInput) edi.getEditorInput();
				final IFile file = inp.getFile();

				// FIXME get src folder from projectsettings
				final Pattern pattern = Pattern.compile(BundleNode.SRC_FOLDER + "(.*?)\\.java"); //$NON-NLS-1$
				final Matcher matcher = pattern.matcher(file.getProjectRelativePath().toOSString());
				if (matcher.matches()) {
					final String cleanClassName = matcher.group(1).substring(1).replace("\\", "."); //$NON-NLS-1$ //$NON-NLS-2$
					final AssemblyModel model = Activator.getDefault().getAssemblyModel();
					final SubModuleNode subMod = Activator.getDefault().getModelService()
							.findSubModuleByClassName(model, file.getProject(), cleanClassName);

					if (null != subMod) {
						if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow()) {
							final AssemblyView assView = (AssemblyView) PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage().findView(AssemblyView.ID);
							if (null != assView) {
								assView.selectNode(subMod);
							}
						}
					}
				}
			}
		}
	}
}
