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
package org.eclipse.riena.toolbox.previewer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.riena.toolbox.previewer.WorkspaceClassLoader;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;
import org.eclipse.riena.toolbox.previewer.ui.Preview;
import org.eclipse.riena.toolbox.previewer.ui.WorkbenchUtil;

/**
 * Shows the content of the active editor in the {@link Preview}
 * 
 */
@SuppressWarnings("restriction")
public class PreviewActiveEditorHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);

		if (activeEditor instanceof CompilationUnitEditor) {
			final IWorkingCopyManager manager = JavaPlugin.getDefault().getWorkingCopyManager();
			final ICompilationUnit unit = manager.getWorkingCopy(activeEditor.getEditorInput());
			final ViewPartInfo viewPart = WorkspaceClassLoader.getInstance().loadClass(unit);
			WorkbenchUtil.showView(viewPart);
		}

		return null;
	}

}
