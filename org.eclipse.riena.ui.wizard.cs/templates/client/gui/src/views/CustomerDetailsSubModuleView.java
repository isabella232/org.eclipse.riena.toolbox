package ${package}.views;

import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import ${package}.controllers.CustomerDetailsSubModuleController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class CustomerDetailsSubModuleView extends SubModuleView<CustomerDetailsSubModuleController> implements ViewConstants {
	public final static String ID = CustomerDetailsSubModuleView.class.getName();

	private Composite contentArea;

	@Override
	protected void basicCreatePartControl(Composite parent) {
		this.contentArea = parent;
		contentArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		FormLayout layout = new FormLayout();
		layout.marginWidth = layout.marginHeight = 10;
		contentArea.setLayout(layout);

		Label customerLabel = new Label(parent, SWT.LEFT);
		customerLabel.setText("Customer");
		customerLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
		customerLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		customerLabel.setLayoutData(fd);

		Label customerNumberLabel = new Label(contentArea, SWT.LEFT);
		customerNumberLabel.setText("Number:"); 
		customerNumberLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(customerLabel, 30, SWT.RIGHT);
		fd.width = LABEL_WIDTH;
		customerNumberLabel.setLayoutData(fd);

		Text customerNumberText = new Text(contentArea, SWT.SINGLE);
		addUIControl(customerNumberText, CustomerDetailsSubModuleController.RIDGET_ID_CUSTOMER_NUMBER);
		fd = new FormData();
		fd.top = new FormAttachment(customerNumberLabel, 0, SWT.CENTER);
		fd.left = new FormAttachment(customerNumberLabel, 5, SWT.RIGHT);
		fd.width = FIELD_WIDTH;
		customerNumberText.setLayoutData(fd);

		Label lastNameLabel = new Label(contentArea, SWT.LEFT);
		lastNameLabel.setText("Last Name:");
		lastNameLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		fd = new FormData();
		fd.top = new FormAttachment(customerNumberLabel, 20, SWT.BOTTOM);
		fd.left = new FormAttachment(customerNumberLabel, 0, SWT.LEFT);
		fd.width = LABEL_WIDTH;
		lastNameLabel.setLayoutData(fd);

		Text lastNameText = new Text(contentArea, SWT.BORDER | SWT.SINGLE);
		addUIControl(lastNameText, CustomerDetailsSubModuleController.RIDGET_ID_LAST_NAME);
		fd = new FormData();
		fd.top = new FormAttachment(lastNameLabel, 0, SWT.CENTER);
		fd.left = new FormAttachment(lastNameLabel, 5, SWT.RIGHT);
		fd.right = new FormAttachment(customerNumberText, 0, SWT.RIGHT);
		lastNameText.setLayoutData(fd);

		Label firstNameLabel = new Label(contentArea, SWT.LEFT);
		firstNameLabel.setText("First Name:"); 
		firstNameLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		fd = new FormData();
		fd.top = new FormAttachment(lastNameText, 0, SWT.CENTER);
		fd.left = new FormAttachment(lastNameText, 20, SWT.RIGHT);
		fd.width = LABEL_WIDTH;
		firstNameLabel.setLayoutData(fd);

		Text firstnameText = new Text(contentArea, SWT.BORDER | SWT.SINGLE);
		addUIControl(firstnameText, CustomerDetailsSubModuleController.RIDGET_ID_FIRST_NAME);
		fd = new FormData();
		fd.top = new FormAttachment(firstNameLabel, 0, SWT.CENTER);
		fd.left = new FormAttachment(firstNameLabel, 0, SWT.RIGHT);
		fd.width = FIELD_WIDTH;
		firstnameText.setLayoutData(fd);

		Label birthdayLabel = new Label(contentArea, SWT.LEFT);
		birthdayLabel.setText("Birthday:"); 
		birthdayLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		fd = new FormData();
		fd.top = new FormAttachment(firstNameLabel, 10, SWT.BOTTOM);
		fd.left = new FormAttachment(lastNameLabel, 0, SWT.LEFT);
		birthdayLabel.setLayoutData(fd);

		DateTime birthdayText = new DateTime(contentArea, SWT.BORDER | SWT.SINGLE);
		fd = new FormData();
		fd.top = new FormAttachment(birthdayLabel, 0, SWT.TOP);
		fd.left = new FormAttachment(lastNameText, 0, SWT.LEFT);
		fd.right = new FormAttachment(lastNameText, 0, SWT.RIGHT);
		birthdayText.setLayoutData(fd);

		Label birthplaceLabel = new Label(contentArea, SWT.LEFT);
		birthplaceLabel.setText("Birthplace:"); 
		birthplaceLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		fd = new FormData();
		fd.top = new FormAttachment(birthdayText, 0, SWT.CENTER);
		fd.left = new FormAttachment(birthdayText, 20, SWT.RIGHT);
		fd.right = new FormAttachment(firstNameLabel, 0, SWT.RIGHT);
		birthplaceLabel.setLayoutData(fd);

		Text birthplaceText = new Text(contentArea, SWT.BORDER | SWT.SINGLE);
		addUIControl(birthplaceText, CustomerDetailsSubModuleController.RIDGET_ID_BIRTHPLACE);
		fd = new FormData();
		fd.top = new FormAttachment(birthplaceLabel, 0, SWT.CENTER);
		fd.left = new FormAttachment(birthplaceLabel, 0, SWT.RIGHT);
		fd.right = new FormAttachment(firstnameText, 0, SWT.RIGHT);
		birthplaceText.setLayoutData(fd);

		Button closeButton = new Button(contentArea, 0);
		addUIControl(closeButton, CustomerDetailsSubModuleController.RIDGET_ID_CLOSE);
		fd = new FormData();
		fd.top = new FormAttachment(birthplaceText, 25, SWT.BOTTOM);
		fd.right = new FormAttachment(birthplaceText, 0, SWT.RIGHT);
		fd.width = FIELD_WIDTH;
		closeButton.setLayoutData(fd);

		Button saveButton = new Button(contentArea, 0);
		addUIControl(saveButton, CustomerDetailsSubModuleController.RIDGET_ID_SAVE);
		fd = new FormData();
		fd.top = new FormAttachment(birthplaceText, 25, SWT.BOTTOM);
		fd.right = new FormAttachment(closeButton, -5, SWT.LEFT);
		fd.width = FIELD_WIDTH;

		saveButton.setLayoutData(fd);
	}

	public Image getIcon() {
		return AbstractUIPlugin.imageDescriptorFromPlugin("${project}", "/icons/user_16.png")
				.createImage();
	}

	@Override
	protected CustomerDetailsSubModuleController createController(ISubModuleNode subModuleNode) {
		return new CustomerDetailsSubModuleController(subModuleNode);
	}
}
