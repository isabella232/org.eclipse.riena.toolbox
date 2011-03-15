package org.eclipse.riena.toolbox.previewer.ui;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.eclipse.riena.toolbox.internal.previewer.Activator;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;

public class WorkbenchUtil {

	public static void showView(final ViewPartInfo info) {
		if (null == info) {
			return;
		}

		try {
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final Preview previewer = (Preview) activePage.showView(Preview.ID);
			previewer.showView(info);
			previewer.setFocus();
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	public static void handleException(final Exception e) {
		Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		throw new RuntimeException(e);
	}
}
