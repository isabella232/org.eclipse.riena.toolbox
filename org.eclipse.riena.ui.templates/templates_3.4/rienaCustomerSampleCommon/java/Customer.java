package $packageName$;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Class containing a customer
 */
public class Customer {

	public final static String PROPERTY_CUSTOMER_NUMBER = "customerNumber";
	public final static String PROPERTY_FORM = "form";
	public final static String PROPERTY_TITLE = "title";
	public final static String PROPERTY_FIRST_NAME = "firstName";
	public final static String PROPERTY_LAST_NAME = "lastName";
	public final static String PROPERTY_ADDRESS = "address";
	public final static String PROPERTY_BIRTH = "birth";
	public final static String PROPERTY_PHONE_PRIVATE = "phonePrivate";
	public final static String PROPERTY_PHONE_BUSINESS = "phoneBusiness";
	public final static String PROPERTY_PHONE_MOBILE = "phoneMobile";
	public final static String PROPERTY_FAX = "fax";
	public final static String PROPERTY_EMAIL = "email";
	public final static String PROPERTY_BANK_DATA = "bankData";

	/**
	 * db4o - id
	 */
	private long id = -1;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	private transient PropertyChangeSupport propertyChangeSupport;

	private Integer customerNumber;
	private String form;
	private String title;
	private String firstName;
	private String lastName;
	private Address address;
	private Birth birth;
	private String phonePrivate;
	private String phoneBusiness;
	private String phoneMobile;
	private String fax;
	private String email;
	private List<BankData> bankData;

	// private List<Offer> offers;

	/**
	 * Creates an empty customer
	 */
	public Customer() {

		super();

		propertyChangeSupport = new PropertyChangeSupport(this);
		address = new Address();
		birth = new Birth();
		// offers = new ArrayList<Offer>();
		bankData = new ArrayList<BankData>();
		firstName = "";
		lastName = "";
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Returns the address of the customer
	 * 
	 * @return address of the customer
	 */
	public Address getAddress() {

		return address;

	} // end method

	/**
	 * Sets the address of the customer
	 * 
	 * @param address
	 *            of the customer
	 */
	public void setAddress(Address address) {
		Address old = this.getAddress();
		this.address = address;
		firePropertyChanged(PROPERTY_ADDRESS, old, address);

	} // end method

	/**
	 * Returns the birth of the customer
	 * 
	 * @return birth of the customer
	 */
	public Birth getBirth() {

		return birth;

	} // end method

	/**
	 * Sets the birth of the customer
	 * 
	 * @param birth
	 *            - the birth to set
	 */
	public void setBirth(Birth birth) {
		Birth old = getBirth();
		this.birth = birth;
		firePropertyChanged(PROPERTY_BIRTH, old, birth);

	} // end method

	/**
	 * Returns the first name of the customer
	 * 
	 * @return first name of the customer
	 */
	public String getFirstName() {

		return firstName;

	} // end method

	/**
	 * Sets the first name of the customer
	 * 
	 * @param firstName
	 *            - the first name to set
	 */
	public void setFirstName(String firstName) {
		String old = this.getFirstName();
		this.firstName = firstName;
		firePropertyChanged(PROPERTY_FIRST_NAME, old, firstName);

	} // end method

	/**
	 * Returns the form of the customer
	 * 
	 * @return form of the customer
	 */
	public String getForm() {

		return form;

	} // end method

	/**
	 * Sets the form of the customer
	 * 
	 * @param form
	 *            - the form to set.
	 */
	public void setForm(String form) {
		String old = getForm();
		this.form = form;
		firePropertyChanged(PROPERTY_FORM, old, form);

	} // end method

	/**
	 * Returns the last name of the customer
	 * 
	 * @return last name of the customer
	 */
	public String getLastName() {

		return lastName;

	} // end method

	/**
	 * Sets the last name of the customer
	 * 
	 * @param lastName
	 *            - the last name to set
	 */
	public void setLastName(String lastName) {
		String old = getLastName();
		this.lastName = lastName;
		firePropertyChanged(PROPERTY_LAST_NAME, old, lastName);

	} // end method

	/**
	 * Returns the title of the customer
	 * 
	 * @return title of the customer
	 */
	public String getTitle() {

		return title;

	} // end method

	/**
	 * Set the title of the customer
	 * 
	 * @param title
	 *            - the title to set
	 */
	public void setTitle(String title) {
		String old = getTitle();
		this.title = title;
		firePropertyChanged(PROPERTY_TITLE, old, title);

	} // end method

	/**
	 * Returns the fax number of the customer
	 * 
	 * @return fax number
	 */
	public String getFax() {

		return fax;

	} // end method

	/**
	 * Sets the given fax number
	 * 
	 * @param fax
	 *            - the fax number to set
	 */
	public void setFax(String fax) {
		String old = getFax();
		this.fax = fax;
		firePropertyChanged(PROPERTY_FAX, old, fax);

	} // end method

	/**
	 * Returns the business phone number of the customer
	 * 
	 * @return business phone number
	 */
	public String getPhoneBusiness() {

		return phoneBusiness;

	} // end method

	/**
	 * Sets the given business phone number
	 * 
	 * @param phoneBusiness
	 *            - the phone number to set
	 */
	public void setPhoneBusiness(String phoneBusiness) {
		String old = getPhoneBusiness();
		this.phoneBusiness = phoneBusiness;
		firePropertyChanged(PROPERTY_PHONE_BUSINESS, old, phoneBusiness);

	} // end method

	/**
	 * Returns the mobile phone number of the customer
	 * 
	 * @return mobile phone number
	 */
	public String getPhoneMobile() {

		return phoneMobile;

	} // end method

	/**
	 * Sets the given mobile phone number
	 * 
	 * @param phoneMobile
	 *            - the phone number to set
	 */
	public void setPhoneMobile(String phoneMobile) {
		String old = getPhoneMobile();
		this.phoneMobile = phoneMobile;
		firePropertyChanged(PROPERTY_PHONE_MOBILE, old, phoneMobile);

	} // end method

	/**
	 * Returns the private phone number of the customer
	 * 
	 * @return private phone number
	 */
	public String getPhonePrivate() {

		return phonePrivate;

	} // end method

	/**
	 * Sets the given private phone number
	 * 
	 * @param phonePrivate
	 *            - the phone number to set
	 */
	public void setPhonePrivate(String phonePrivate) {
		String old = getPhonePrivate();
		this.phonePrivate = phonePrivate;
		firePropertyChanged(PROPERTY_PHONE_PRIVATE, old, phonePrivate);

	} // end method

	/**
	 * Returns the eMail address of the customer
	 * 
	 * @return eMail address
	 */
	public String getEmail() {

		return email;

	} // end method

	/**
	 * Sets the given eMail address
	 * 
	 * @param email
	 *            - the eMail address to set
	 */
	public void setEmail(String email) {
		String old = getEmail();
		this.email = email;
		firePropertyChanged(PROPERTY_EMAIL, old, email);

	} // end method

	/**
	 * @return Returns the customerNumber.
	 */
	public Integer getCustomerNumber() {

		return customerNumber;

	} // end method

	/**
	 * @param customerNumber
	 *            The customerNumber to set.
	 */
	public void setCustomerNumber(Integer customerNumber) {

		Integer old = getCustomerNumber();
		this.customerNumber = customerNumber;
		firePropertyChanged(PROPERTY_CUSTOMER_NUMBER, old, customerNumber);
	} // end method

	/**
	 * Compares two customers by customerNumber.
	 * 
	 * @param obj
	 *            The reference object with which to compare.
	 * @return <code>true</code> if the argument is a Customer with the same
	 *         customerNumber (other than <code>null</code>);<code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Customer) {
			Customer customer = (Customer) obj;
			if (customerNumber != null && customerNumber.equals(customer.getCustomerNumber())) {
				return true;
			}
		}
		return false;

	} // end method

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		if (customerNumber != null) {
			return customerNumber.hashCode();
		}
		return 0;

	} // end method

	/**
	 * Returns a list with all bank data
	 * 
	 * @return list of bank data
	 */
	public List<BankData> getBankData() {

		return bankData;

	} // end method

	/**
	 * Sets the given list of bank data
	 * 
	 * @param bankData
	 *            - the list of bank data to set
	 */
	public void setBankData(List<BankData> bankData) {
		List<BankData> old = getBankData();
		this.bankData = bankData;
		firePropertyChanged(PROPERTY_BANK_DATA, old, bankData);

	} // end method

	@Override
	public String toString() {
		return getFullCustomerName() + " --> customer number=" + this.getCustomerNumber();
	}

	public String getFullCustomerName() {

		StringBuilder builder = new StringBuilder();

		if (getLastName() != null) {
			builder.append(getLastName());
			builder.append(", ");
		}
		builder.append(getFirstName());

		return builder.toString();
	}
} // end class
