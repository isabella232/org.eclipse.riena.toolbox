package org.eclipse.riena.toolbox.previewer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.riena.toolbox.previewer.ClassFinder;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;
import org.eclipse.riena.toolbox.previewer.ui.WorkbenchUtil;

public class PreviewPackageExplorerSelectionHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ClassFinder rcpPreviewer = new ClassFinder();
		ViewPartInfo viewPart = rcpPreviewer.loadClass(rcpPreviewer.getSelectionFromPackageExplorer());
		WorkbenchUtil.showView(viewPart);
		return null;
	}
}

