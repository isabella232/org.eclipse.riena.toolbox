package ${package}.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.riena.core.wire.InjectService;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import ${package}.views.CustomerDetailsSubModuleView;
import ${package.common}.Customer;
import ${package.common}.ICustomerSearch;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;
import org.eclipse.riena.ui.workarea.WorkareaManager;

public class CustomerSearchSubModuleController extends SubModuleController {
	private ICustomerSearch service;

	private ITableRidget resultTable;
	private IActionRidget searchAction, clearAction, openAction;

	private ITextRidget firstNameRidget, lastNameRidget;

	private WritableValue selection;

	private Customer sample;

	private List<Customer> list = new ArrayList<Customer>();

	public CustomerSearchSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);

		sample = new Customer();
	}

	@InjectService(service = ICustomerSearch.class)
	public void bind(ICustomerSearch service) {
		this.service = service;
	}

	public void unbind(ICustomerSearch service) {
		if (this.service == service)
			this.service = null;
	}

	@Override
	public void configureRidgets() {
		resultTable = getRidget(ITableRidget.class, "tableRidget");
		searchAction = getRidget(IActionRidget.class, "searchAction");
		clearAction = getRidget(IActionRidget.class, "clearAction");
		openAction = getRidget(IActionRidget.class, "openAction");
		firstNameRidget = getRidget(ITextRidget.class, "firstNameRidget");
		lastNameRidget = getRidget(ITextRidget.class, "lastNameRidget");
	}

	@Override
	public void afterBind() {
		super.afterBind();
		String[] columnProperties = new String[] { Customer.PROPERTY_CUSTOMER_NUMBER, Customer.PROPERTY_LAST_NAME, Customer.PROPERTY_FIRST_NAME, Customer.PROPERTY_PHONE_BUSINESS };
		String[] columnHeaders = new String[] { "Number", "Last Name", "First Name", "Phone" };
		Assert.isTrue(columnHeaders.length == columnProperties.length);

		resultTable.bindToModel(this, "list", Customer.class, columnProperties, columnHeaders);
		resultTable.updateFromModel();
		resultTable.addDoubleClickListener(new OpenListener());

		selection = new WritableValue();
		resultTable.bindSingleSelectionToModel(selection);

		searchAction.addListener(new SearchListener());
		firstNameRidget.bindToModel(sample, "firstName");
		lastNameRidget.bindToModel(sample, "lastName");
		clearAction.addListener(new ClearListener());
		openAction.addListener(new OpenListener());
	}

	private void openSelected() {
		Object selectedValue = selection.getValue();
		if (selectedValue == null)
			return;

		if (!(selectedValue instanceof Customer))
			throw new RuntimeException("Invalid datatype for selected value");

		Customer selected = (Customer) selectedValue;

		SubModuleNode child = new SubModuleNode(null, selected.getFirstName());

		child.setContext(Customer.class.getName(), selected);

		WorkareaManager.getInstance().registerDefinition(child, CustomerDetailsSubModuleView.ID);

		getNavigationNode().addChild(child);
		child.activate();
	}

	public List<Customer> getList() {
		return list;
	}

	private class SearchListener implements IActionListener {
		public void callback() {
			Customer[] result = service.findCustomer(sample);
			list = Arrays.asList(result);

			resultTable.updateFromModel();
		}
	}

	private class ClearListener implements IActionListener {
		public void callback() {
			sample.setFirstName("");
			sample.setLastName("");

			firstNameRidget.updateFromModel();
			lastNameRidget.updateFromModel();
		}
	}

	private class OpenListener implements IActionListener {
		public void callback() {
			openSelected();
		}
	}
}
