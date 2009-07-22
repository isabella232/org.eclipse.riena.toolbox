package ${package}.controllers;

import org.eclipse.riena.core.wire.InjectService;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import ${package.common}.Customer;
import ${package.common}.ICustomers;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;

public class CustomerDetailsSubModuleController extends SubModuleController {

	public static final String RIDGET_ID_CUSTOMER_NUMBER = "customerNumber";
	public static final String RIDGET_ID_LAST_NAME = "lastName";
	public static final String RIDGET_ID_FIRST_NAME = "firstName";
	public static final String RIDGET_ID_BIRTHPLACE = "birthPlace";
	public static final String RIDGET_ID_SAVE = "save";
	public static final String RIDGET_ID_CLOSE = "close";
	
	private ICustomers service;

	public CustomerDetailsSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
	}

	
	@InjectService(service = ICustomers.class)
	public void bind(ICustomers service) {
		this.service = service;
	}

	public void unbind(ICustomers service) {
		if (this.service == service)
			this.service = null;
	}

	
	@Override
	public void configureRidgets() {
		super.configureRidgets();

		Customer customer = getCustomer();

		ITextRidget customerNumber = (ITextRidget) getRidget(RIDGET_ID_CUSTOMER_NUMBER);
		customerNumber.setOutputOnly(true);
		customerNumber.bindToModel(customer, "customerNumber");
		customerNumber.updateFromModel();

		ITextRidget lastName = (ITextRidget) getRidget(RIDGET_ID_LAST_NAME);
		lastName.bindToModel(customer, "lastName");
		lastName.updateFromModel();

		ITextRidget firstName = (ITextRidget) getRidget(RIDGET_ID_FIRST_NAME);
		firstName.bindToModel(customer, "firstName");
		firstName.updateFromModel();

		ITextRidget birthPlace = (ITextRidget) getRidget(RIDGET_ID_BIRTHPLACE);
		birthPlace.bindToModel(customer.getBirth(), "birthPlace");
		birthPlace.updateFromModel();

		IActionRidget saveAction = (IActionRidget) getRidget(RIDGET_ID_SAVE);
		saveAction.addListener(new SaveCallback());
		saveAction.setText("Save");

		IActionRidget closeAction = (IActionRidget) getRidget(RIDGET_ID_CLOSE);
		closeAction.addListener(new CloseCallback());
		closeAction.setText("Close");
	}

	private Customer getCustomer() {
		return (Customer) getNavigationNode().getContext(Customer.class.getName());
	}

	private  class SaveCallback implements IActionListener {
		public void callback() {
			service.store(getCustomer());
		}
	}

	private class CloseCallback implements IActionListener {

		public void callback() {
			getNavigationNode().dispose();
		}
	}
}
