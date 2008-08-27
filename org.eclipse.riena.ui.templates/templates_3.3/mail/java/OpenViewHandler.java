package $packageName$;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;


public class OpenViewHandler extends AbstractHandler implements IHandler {

	private int instanceNum = 0;

	public Object execute(ExecutionEvent event) {
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage().showView(View.ID,
					Integer.toString(instanceNum++), IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Error opening view:"
					+ e.getMessage());
		} catch (ExecutionException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Error opening view:"
					+ e.getMessage());
		}
		return null;
	}
}
