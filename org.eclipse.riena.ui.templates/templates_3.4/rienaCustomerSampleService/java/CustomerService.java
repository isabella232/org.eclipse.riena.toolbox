package $packageName$;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Customers Service Class that is exposed as Webservice. It implements
 * ICustomers (the customer maintaince interface) and ICustomerSearch (the
 * interface for search on the customer data)
 */
public class CustomerService implements ICustomerService {

	private Map<Integer, Customer> customers;
	private int nextUniqueCustomerNumber;

	public CustomerService() {
		customers = new HashMap<Integer, Customer>();

		nextUniqueCustomerNumber = 1;
		initializeCustomers();
	}
	
	/**
	 * @see org.eclipse.riena.sample.app.common.model.ICustomers#getNextUniqueCustomerNumber()
	 */
	public Integer getNextUniqueCustomerNumber() {

		return nextUniqueCustomerNumber++;
	}


	/**
	 * @see org.eclipse.riena.sample.app.common.model.ICustomers#store(org.eclipse.riena.sample.app.common.model.Customer)
	 */
	public void store(Customer customer) {
		storeInternal(customer);
	}

	/**
	 * This method is also used internally to store customers. No security is
	 * checked.
	 * 
	 * @param customer
	 */
	private void storeInternal(Customer customer) {
		customer.setId(customer.getCustomerNumber());
		customers.put(customer.getCustomerNumber(), customer);

	}

	/**
	 * @see org.eclipse.riena.sample.app.common.model.ICustomerSearch#findCustomer(org.eclipse.riena.sample.app.common.model.Customer)
	 */
	public Customer[] findCustomer(Customer searchedCustomer) {
		List<Customer> l = new ArrayList<Customer>();

		for (Customer c : customers.values()) {
			if (isIdentical(c, searchedCustomer)) {
				l.add(c);
			}
		}

		return l.toArray(new Customer[l.size()]);
	}

	private boolean isIdentical(Customer customer, Customer searchedCustomer) {

		if (searchedCustomer.getCustomerNumber() != null
				&& !searchedCustomer.getCustomerNumber().equals(customer.getCustomerNumber())) {
			return false;
		}

		if (!contains(customer.getLastName(), searchedCustomer.getLastName())) {
			return false;
		}

		if (!contains(customer.getFirstName(), searchedCustomer.getFirstName())) {
			return false;
		}

		return true;
	}

	private boolean contains(String original, String other) {

		if (other == null || other.equals("")) {
			return true;
		}

		return original.toUpperCase().contains(other.toUpperCase());
	}

	private void initializeCustomers() {

		Customer customer = new Customer();
		customer.setFirstName("Han");
		customer.setLastName("Solo");
		Address address = new Address();
		address.setCity("Frankfurt am Main");
		address.setStreet("Am Main 233");
		address.setZipCode("61236");
		address.setCountry("Germany");
		customer.setAddress(address);

		customer.setBirth(new Birth());
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		try {
			customer.getBirth().setBirthDay(format.parse("01.04.1962"));
		} catch (ParseException e) {
			// TODO Throw exception
			e.printStackTrace();
		}
		customer.getBirth().setBirthPlace("Frankfurt");
		customer.setBankData(new ArrayList<BankData>());
		initializeCustomerNumber(customer);

		customer = new Customer();
		customer.setFirstName("Luke");
		customer.setLastName("Skywalker");
		address = new Address();
		address.setCity("Washington");
		address.setStreet("Any Road 845");
		address.setZipCode("98123898");
		address.setCountry("USA");
		customer.setAddress(address);
		customer.setBirth(new Birth());
		try {
			customer.getBirth().setBirthDay(format.parse("01.04.1963"));
		} catch (ParseException e) {
			// TODO Throw exception
			e.printStackTrace();
		}
		customer.getBirth().setBirthPlace("Frankfurt");
		customer.setBankData(new ArrayList<BankData>());
		initializeCustomerNumber(customer);

		customer = new Customer();
		customer.setFirstName("Frodo");
		customer.setLastName("Baggins");
		address = new Address();
		address.setCity("Hanau");
		address.setStreet("Grüner Weg 3");
		address.setZipCode("62342");
		address.setCountry("Germany");
		customer.setAddress(address);
		customer.setBirth(new Birth());
		try {
			customer.getBirth().setBirthDay(format.parse("01.04.1964"));
		} catch (ParseException e) {
			// TODO Throw exception
			e.printStackTrace();
		}
		customer.getBirth().setBirthPlace("Frankfurt");
		customer.setBankData(new ArrayList<BankData>());
		initializeCustomerNumber(customer);
	}

	private void initializeCustomerNumber(Customer customer) {

		customer.setCustomerNumber(getNextUniqueCustomerNumber());
		storeInternal(customer);
	}
}
