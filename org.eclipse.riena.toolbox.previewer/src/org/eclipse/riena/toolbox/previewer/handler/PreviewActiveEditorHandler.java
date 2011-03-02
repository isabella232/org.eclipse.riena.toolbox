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

import org.eclipse.riena.toolbox.previewer.ClassFinder;
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
			final ViewPartInfo viewPart = new ClassFinder().loadClass(unit);
			WorkbenchUtil.showView(viewPart);
		}

		return null;
	}

}
