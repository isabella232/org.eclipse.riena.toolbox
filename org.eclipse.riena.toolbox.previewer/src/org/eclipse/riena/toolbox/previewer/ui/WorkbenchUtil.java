package org.eclipse.riena.toolbox.previewer.ui;

import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class WorkbenchUtil {
	
	public static void showView(ViewPartInfo info) {
		if (null == info){
			return;
		}

		try {
			IWorkbenchPage activePage =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			Preview previewer = (Preview) activePage.showView(Preview.ID);
			previewer.showView(info);
			previewer.setFocus();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
