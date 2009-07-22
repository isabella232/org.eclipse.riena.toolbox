package ${package};

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

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

	private transient PropertyChangeSupport propertyChangeSupport;

	private long id = -1;

	private Integer customerNumber;
	private String form;
	private String title;
	private String firstName;
	private String lastName;
	private Address address;
	private BirthInfo birth;
	private String phonePrivate;
	private String phoneBusiness;
	private String phoneMobile;
	private String fax;
	private String email;
	private List<BankData> bankData;

	public Customer() {
		super();

		propertyChangeSupport = new PropertyChangeSupport(this);
		address = new Address();
		birth = new BirthInfo();
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

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public Address getAddress() {

		return address;

	}

	public void setAddress(Address address) {
		Address old = this.getAddress();
		this.address = address;
		firePropertyChanged(PROPERTY_ADDRESS, old, address);

	}

	public BirthInfo getBirth() {

		return birth;

	}

	public void setBirth(BirthInfo birth) {
		BirthInfo old = getBirth();
		this.birth = birth;
		firePropertyChanged(PROPERTY_BIRTH, old, birth);

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		String old = this.getFirstName();
		this.firstName = firstName;
		firePropertyChanged(PROPERTY_FIRST_NAME, old, firstName);
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		String old = getForm();
		this.form = form;
		firePropertyChanged(PROPERTY_FORM, old, form);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		String old = getLastName();
		this.lastName = lastName;
		firePropertyChanged(PROPERTY_LAST_NAME, old, lastName);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String old = getTitle();
		this.title = title;
		firePropertyChanged(PROPERTY_TITLE, old, title);
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		String old = getFax();
		this.fax = fax;
		firePropertyChanged(PROPERTY_FAX, old, fax);
	}

	public String getPhoneBusiness() {
		return phoneBusiness;
	}

	public void setPhoneBusiness(String phoneBusiness) {
		String old = getPhoneBusiness();
		this.phoneBusiness = phoneBusiness;
		firePropertyChanged(PROPERTY_PHONE_BUSINESS, old, phoneBusiness);
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		String old = getPhoneMobile();
		this.phoneMobile = phoneMobile;
		firePropertyChanged(PROPERTY_PHONE_MOBILE, old, phoneMobile);
	}

	public String getPhonePrivate() {
		return phonePrivate;
	}

	public void setPhonePrivate(String phonePrivate) {
		String old = getPhonePrivate();
		this.phonePrivate = phonePrivate;
		firePropertyChanged(PROPERTY_PHONE_PRIVATE, old, phonePrivate);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		String old = getEmail();
		this.email = email;
		firePropertyChanged(PROPERTY_EMAIL, old, email);
	}

	public Integer getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(Integer customerNumber) {

		Integer old = getCustomerNumber();
		this.customerNumber = customerNumber;
		firePropertyChanged(PROPERTY_CUSTOMER_NUMBER, old, customerNumber);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Customer) {
			Customer customer = (Customer) obj;
			if (customerNumber != null && customerNumber.equals(customer.getCustomerNumber())) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		if (customerNumber != null) {
			return customerNumber.hashCode();
		}
		return 0;
	}

	public List<BankData> getBankData() {
		return bankData;
	}

	public void setBankData(List<BankData> bankData) {
		List<BankData> old = getBankData();
		this.bankData = bankData;
		firePropertyChanged(PROPERTY_BANK_DATA, old, bankData);
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
}
