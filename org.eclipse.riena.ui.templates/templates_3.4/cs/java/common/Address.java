/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package $packageName$.common;

/**
 * Class containing an address
 */
public class Address implements Cloneable {

	private String street;
	private String zipCode;
	private String city;
	private String country;

	/**
	 * Creates an empty address
	 */
	public Address() {

		super();

	} // end constructor

	/**
	 * Creates an address and sets the given values
	 * 
	 * @param country
	 *            - the country to set
	 * @param zipCode
	 *            - the zip code to set
	 * @param city
	 *            - the city to set
	 * @param street
	 *            - the street to set
	 */
	public Address(String country, String zipCode, String city, String street) {

		this();

		this.country = country;
		this.zipCode = zipCode;
		this.city = city;
		this.street = street;

	} // end cosntructor

	/**
	 * Returns the city
	 * 
	 * @return city
	 */
	public String getCity() {

		return city;

	} // end method

	/**
	 * Sets the given city
	 * 
	 * @param city
	 *            - the city to set
	 */
	public void setCity(String city) {

		this.city = city;

	} // end method

	/**
	 * Returns the street
	 * 
	 * @return street
	 */
	public String getStreet() {

		return street;

	} // end method

	/**
	 * Sets the given street
	 * 
	 * @param street
	 *            - the street to set
	 */
	public void setStreet(String street) {

		this.street = street;

	} // end method

	/**
	 * Returns the zip code
	 * 
	 * @return zip code
	 */
	public String getZipCode() {

		return zipCode;

	} // end method

	/**
	 * Sets the given zip code
	 * 
	 * @param zipCode
	 *            - the zip code to set
	 */
	public void setZipCode(String zipCode) {

		this.zipCode = zipCode;

	} // end method

	/**
	 * Returns the country
	 * 
	 * @return country
	 */
	public String getCountry() {

		return country;

	} // end method

	/**
	 * Sets the given country
	 * 
	 * @param country
	 *            - the country to set
	 */
	public void setCountry(String country) {

		this.country = country;

	} // end method

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		super.clone();
		Address clone = new Address();
		clone.setCity(getCity());
		clone.setCountry(getCountry());
		clone.setStreet(getStreet());
		clone.setZipCode(getZipCode());

		return clone;

	} // end method

} // end class
