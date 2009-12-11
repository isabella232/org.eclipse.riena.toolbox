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

import java.util.Date;

/**
 * Class containing a birth
 */
public class Birth {

	private Date birthDay;
	private String birthPlace;

	/**
	 * Creates an empty birth
	 */
	public Birth() {

		super();

	} // end constructor

	/**
	 * Creates a birth and sets the given values
	 * 
	 * @param day
	 *            - birthday to set
	 * @param place
	 *            - birthplace to set
	 */
	public Birth(Date day, String place) {

		this();

		this.birthDay = day;
		this.birthPlace = place;

	} // end constructor

	/**
	 * Returns the birthday
	 * 
	 * @return birthday
	 */
	public Date getBirthDay() {

		return birthDay;

	} // end method

	/**
	 * Sets the given birthday
	 * 
	 * @param day
	 *            - the birth day to set.
	 */
	public void setBirthDay(Date day) {

		this.birthDay = day;

	} // end method

	/**
	 * Returns the birth place
	 * 
	 * @return birthplace.
	 */
	public String getBirthPlace() {

		return birthPlace;

	} // end method

	/**
	 * Sets the given birth place
	 * 
	 * @param birthPlace
	 *            - the birthplace to set.
	 */
	public void setBirthPlace(String birthPlace) {

		this.birthPlace = birthPlace;
	
	} // end method

} // end class
