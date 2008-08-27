package $packageName$;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;


public class MessagePopupHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Open", "Open Message Dialog!");
		return null;
	}
}