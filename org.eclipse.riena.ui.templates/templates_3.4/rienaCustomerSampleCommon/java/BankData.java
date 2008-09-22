package $packageName$;

/**
 * This class contains bank data
 */
public class BankData implements Cloneable {

	private String accountNumber;
	private String bank;
	private String bankCode;

	/**
	 * Creates empty bank data
	 */
	public BankData() {

		super();

	} // end constructor

	/**
	 * Creates bank data and sets the given values
	 * 
	 * @param accountNumber
	 *            - the accountNumber to set
	 * @param bank
	 *            - the zip bank to set
	 * @param bankCode
	 *            - the bankCode to set
	 */
	public BankData(String accountNumber, String bank, String bankCode) {

		this();

		this.accountNumber = accountNumber;
		this.bank = bank;
		this.bankCode = bankCode;

	} // end cosntructor

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof BankData)) {
			return false;
		}
		BankData bdO = (BankData) other;
		if (bdO.accountNumber.equals(accountNumber) && bdO.bank.equals(bank) && bdO.bankCode.equals(bankCode)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the account number
	 * 
	 * @return account number
	 */
	public String getAccountNumber() {

		return accountNumber;

	} // end method

	/**
	 * Sets the given account number
	 * 
	 * @param accountNumber
	 *            - the account number to set
	 */
	public void setAccountNumber(String accountNumber) {

		this.accountNumber = accountNumber;

	} // end method

	/**
	 * Returns the name of the bank
	 * 
	 * @return name of bank
	 */
	public String getBank() {

		return bank;

	} // end method

	/**
	 * Sets the given name of the bank
	 * 
	 * @param bank
	 *            - the name of bank to set
	 */
	public void setBank(String bank) {

		this.bank = bank;

	} // end method

	/**
	 * Returns the bank code
	 * 
	 * @return Returns the bankCode.
	 */
	public String getBankCode() {

		return bankCode;

	} // end method

	/**
	 * Sets the given bank code
	 * 
	 * @param bankCode
	 *            - the bank codd to set.
	 */
	public void setBankCode(String bankCode) {

		this.bankCode = bankCode;

	} // end method

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String strg = "";
		if (accountNumber != null) {
			strg += accountNumber;
		} else {
			strg += "?";
		} // end if

		strg += " - ";

		if (bank != null) {
			strg += bank;
		} else {
			strg += "?";
		} // end if

		strg += " - ";

		if (bankCode != null) {
			strg += bankCode;
		} else {
			strg += "?";
		} // end if

		return strg;

	} // end method

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		super.clone();
		BankData clone = new BankData();
		clone.setAccountNumber(getAccountNumber());
		clone.setBank(getBank());
		clone.setBankCode(getBankCode());

		return clone;

	} // end method

} // end class
