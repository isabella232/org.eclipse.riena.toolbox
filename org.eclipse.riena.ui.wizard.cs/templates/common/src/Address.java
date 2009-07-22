package ${package};

public class Address {

	private String street;
	private String zipCode;
	private String city;
	private String country;

	public Address() {

	}

	public Address(String country, String zipCode, String city, String street) {
		this.country = country;
		this.zipCode = zipCode;
		this.city = city;
		this.street = street;

	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}