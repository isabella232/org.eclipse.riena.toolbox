package org.eclipse.riena.toolbox.previewer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.riena.toolbox.previewer.WorkspaceClassLoader;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;
import org.eclipse.riena.toolbox.previewer.ui.WorkbenchUtil;

public class PreviewPackageExplorerSelectionHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ViewPartInfo viewPart = WorkspaceClassLoader.getInstance().loadClass(
				WorkspaceClassLoader.getInstance().getSelectionFromPackageExplorer());
		WorkbenchUtil.showView(viewPart);
		return null;
	}
}
