package $PackageName;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.SWT;

public class $ClassName extends SubModuleView {

	@Override
	protected void basicCreatePartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText("$ClassName");
	}
}