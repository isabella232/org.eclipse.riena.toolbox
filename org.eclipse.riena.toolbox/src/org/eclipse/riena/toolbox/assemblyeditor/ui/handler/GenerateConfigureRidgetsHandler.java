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
package org.eclipse.riena.toolbox.assemblyeditor.ui.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.assemblyeditor.ModelService;
import org.eclipse.riena.toolbox.assemblyeditor.RidgetGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.WorkbenchService;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SwtControl;
import org.eclipse.riena.toolbox.assemblyeditor.ui.views.AssemblyView;

/**
 * FIXME: If the Controller is used in more than one nodes, show a
 * selection-dialog and let the user pick one.
 * 
 * TODO: implement PropertyTester for this command
 */
public class GenerateConfigureRidgetsHandler extends AbstractHandler {

	@SuppressWarnings("restriction")
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();

		if (null == activeEditor || !(activeEditor instanceof CompilationUnitEditor)) {
			return null;
		}

		final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(AssemblyView.ID);
		final IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer()
				.getSelection();

		if (null == sel) {
			return null;
		}

		final IWorkingCopyManager manager = JavaPlugin.getDefault().getWorkingCopyManager();
		final ICompilationUnit unit = manager.getWorkingCopy(activeEditor.getEditorInput());
		final RidgetGenerator generator = new RidgetGenerator(unit.getJavaProject().getProject());

		final String className = new WorkbenchService().getCompilationUnitClassNameOfActiveEditor(activeEditor);
		final SubModuleNode selectedNode = new ModelService().findSubModuleByClassName(Activator.getDefault()
				.getAssemblyModel(), unit.getJavaProject().getProject(), className);
		if (null == selectedNode) {
			return null;
		}

		final List<SwtControl> controls = generator.findSwtControlsReflectionStyle(selectedNode.getRcpView()
				.getViewClass());
		generator.generateConfigureRidgets((selectedNode).getController(), controls);

		if (Platform.inDebugMode()) {
			for (final SwtControl control : controls) {
				System.out.println("DEBUG: found control: " + control.getSwtControlClassName() + " ridgetId: "
						+ control.getRidgetId());
			}
		}

		return null;
	}
}
