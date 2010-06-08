package  ${package}.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProvider;
import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import ${package}.controllers.CustomerSearchSubModuleController;
import ${package.common}.Customer;
import org.eclipse.riena.ui.workarea.WorkareaManager;

public class CustomerSearchSubModuleView extends SubModuleView implements ViewConstants {
	public final static String ID = CustomerSearchSubModuleView.class.getName();


	private Table searchResultTable;

	private Text lastNameText, firstNameText;

	@Override
	public void basicCreatePartControl(Composite parent) {
		parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		FormLayout layout = new FormLayout();
		layout.marginWidth = layout.marginHeight = 10;
		parent.setLayout(layout);

		Label lastNameLabel = new Label(parent, SWT.LEFT);
		lastNameLabel.setText("Last Name");
		lastNameLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		FormData  fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		lastNameLabel.setLayoutData(fd);

		lastNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fd = new FormData();
		fd.top = new FormAttachment(lastNameLabel, 0, SWT.CENTER);
		fd.left = new FormAttachment(lastNameLabel, 5, SWT.RIGHT);
		fd.width = FIELD_WIDTH;
		lastNameText.setLayoutData(fd);
		addUIControl(lastNameText, "lastNameRidget");

		Label firstNameLabel = new Label(parent, SWT.LEFT);
		firstNameLabel.setText("First Name");
		firstNameLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		fd = new FormData();
		fd.top = new FormAttachment(lastNameText, 0, SWT.CENTER);
		fd.left = new FormAttachment(lastNameText, 20, SWT.RIGHT);
		firstNameLabel.setLayoutData(fd);

		firstNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fd = new FormData();
		fd.top = new FormAttachment(firstNameLabel, 0, SWT.CENTER);
		fd.left = new FormAttachment(firstNameLabel, 5, SWT.RIGHT);
		fd.width = FIELD_WIDTH;
		firstNameText.setLayoutData(fd);
		addUIControl(firstNameText, "firstNameRidget");

		Button clearButton = new Button(parent, 0);
		clearButton.setText("Clear");
		fd = new FormData();
		fd.top = new FormAttachment(lastNameLabel, 0, SWT.CENTER);
		fd.right = new FormAttachment(100, 0);
		fd.width = FIELD_WIDTH;
		clearButton.setLayoutData(fd);
		addUIControl(clearButton, "clearAction");
		
		Button searchButton = new Button(parent, 0);
		searchButton.setText("Search");
		fd = new FormData();
		fd.top = new FormAttachment(clearButton, 0, SWT.TOP);
		fd.right = new FormAttachment(clearButton, -5, SWT.LEFT);
		fd.width = FIELD_WIDTH;
		searchButton.setLayoutData(fd);
		addUIControl(searchButton, "searchAction");

		// open button
		Button openButton = new Button(parent, 0);
		openButton.setText("Open"); 
		fd = new FormData();
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		fd.width = FIELD_WIDTH;
		openButton.setLayoutData(fd);
		addUIControl(openButton, "openAction"); 
		
		
		// create table
		searchResultTable = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		searchResultTable.setLinesVisible(true);
		// layout table
		fd = new FormData();
		fd.top = new FormAttachment(searchButton, 5, SWT.BOTTOM);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(openButton, -5, SWT.TOP);
		searchResultTable.setLayoutData(fd);

		
		addUIControl(searchResultTable, "tableRidget"); 
		TableColumn customerNumberColumn = new TableColumn(searchResultTable, SWT.CENTER);
		TableColumn lastNameColumn = new TableColumn(searchResultTable, SWT.LEFT);
		TableColumn firstNameColumn = new TableColumn(searchResultTable, SWT.LEFT);
		TableColumn phoneColumn = new TableColumn(searchResultTable, SWT.CENTER);
		customerNumberColumn.setWidth(80);
		firstNameColumn.setWidth(120);
		lastNameColumn.setWidth(120);
		phoneColumn.setWidth(100);
		searchResultTable.setHeaderVisible(true);
		
		parent.setTabList(new Control[] {lastNameText, firstNameText, searchButton, clearButton, searchResultTable, openButton}); 
	}

	private ISubModuleNode getNode() {
		return SwtViewProvider.getInstance().getNavigationNode(this.getViewSite().getId(), this.getViewSite().getSecondaryId(), ISubModuleNode.class);
	}

	protected void openCustomer() {
		Customer selected = ((Customer) searchResultTable.getSelection()[0].getData());

		ISubModuleNode node = getNode();
		SubModuleNode submoduleNode = new SubModuleNode(null, selected.getFirstName());
		submoduleNode.setContext(Customer.class.getName(), selected);

		WorkareaManager.getInstance().registerDefinition(submoduleNode, CustomerDetailsSubModuleView.ID);
		node.addChild(submoduleNode);

		submoduleNode.activate();
	}

	@Override
	public void setFocus() {
		super.setFocus();
	}

	@Override
	protected CustomerSearchSubModuleController createController(ISubModuleNode subModuleNode) {
		return new CustomerSearchSubModuleController(getNode());
	}
}
