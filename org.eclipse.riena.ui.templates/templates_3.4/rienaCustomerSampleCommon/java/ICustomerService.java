package $packageName$;

/**
 * 
 * 
 */
public interface ICustomerService {

	/**
	 * Retrieves customers from the database. For the query the given customer
	 * is use as an example (Query by Example (QBE))
	 * 
	 * @param customer
	 *            customer candidate
	 * @return list of customers
	 */
	Customer[] findCustomer(Customer customer);
	
	void store(Customer customer);

}
