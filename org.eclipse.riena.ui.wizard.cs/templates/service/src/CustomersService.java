package ${package};

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ${package.common}.Address;
import ${package.common}.BankData;
import ${package.common}.BirthInfo;
import ${package.common}.Customer;
import ${package.common}.ICustomerSearch;
import ${package.common}.ICustomers;

public class CustomersService implements ICustomers, ICustomerSearch {
	private Map<Integer, Customer> customers;
	private int customerId;

	public CustomersService() {

		customers = new HashMap<Integer, Customer>();
		customerId = 1;

		try {
			initialize();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Integer getNextUniqueCustomerNumber() {
		return customerId++;
	}

	public void store(Customer customer) {
		customer.setId(customer.getCustomerNumber());
		customers.put(customer.getCustomerNumber(), customer);
	}

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

		if (searchedCustomer.getCustomerNumber() != null && searchedCustomer.getCustomerNumber().equals(customer.getCustomerNumber()))
			return true;

		return contains(customer.getLastName(), searchedCustomer.getLastName()) || contains(customer.getFirstName(), searchedCustomer.getFirstName());
	}

	private boolean contains(String original, String other) {
		if (other == null || other.equals(""))
			return true;

		return original.toUpperCase().contains(other.toUpperCase());
	}

	private void initialize() throws Exception {

		Customer customer = new Customer();
		customer.setFirstName("Han"); 
		customer.setLastName("Solo"); 
		Address address = new Address();
		address.setCity("Frankfurt am Main"); 
		address.setStreet("Am Main 233"); 
		address.setZipCode("61236"); 
		address.setCountry("Germany"); 
		customer.setAddress(address);

		customer.setBirth(new BirthInfo());
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy"); 
		customer.getBirth().setBirthDay(format.parse("01.04.1962")); 
		customer.getBirth().setBirthPlace("Frankfurt"); 
		customer.setBankData(new ArrayList<BankData>());
		createCustomer(customer);

		customer = new Customer();
		customer.setFirstName("Luke"); 
		customer.setLastName("Skywalker"); 
		address = new Address();
		address.setCity("Washington"); 
		address.setStreet("Any Road 845");
		address.setZipCode("98123898");
		address.setCountry("USA"); 
		customer.setAddress(address);
		customer.setBirth(new BirthInfo());
		customer.getBirth().setBirthDay(format.parse("01.04.1963")); 
		customer.getBirth().setBirthPlace("Frankfurt"); 
		customer.setBankData(new ArrayList<BankData>());
		createCustomer(customer);

		customer = new Customer();
		customer.setFirstName("Frodo"); 
		customer.setLastName("Baggins"); 
		address = new Address();
		address.setCity("Hanau"); 
		address.setStreet("Gruener Weg 3"); 
		address.setZipCode("62342"); 
		address.setCountry("Germany");
		customer.setAddress(address);
		customer.setBirth(new BirthInfo());
		customer.getBirth().setBirthDay(format.parse("01.04.1964")); 
		customer.getBirth().setBirthPlace("Frankfurt"); 
		customer.setBankData(new ArrayList<BankData>());
		createCustomer(customer);
	}

	private void createCustomer(Customer customer) {
		customer.setCustomerNumber(getNextUniqueCustomerNumber());
		store(customer);
	}
}
