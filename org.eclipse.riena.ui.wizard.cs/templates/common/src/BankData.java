package ${package};

public class BankData implements Cloneable {

	private String accountNumber;
	private String bank;
	private String bankCode;

	public BankData(String accountNumber, String bank, String bankCode) {
		this.accountNumber = accountNumber;
		this.bank = bank;
		this.bankCode = bankCode;
	}

	public boolean equals(Object other) {
		if (!(other instanceof BankData))
			return false;

		BankData _other = (BankData) other;

		return _other.accountNumber.equals(accountNumber) && _other.bank.equals(bank) && _other.bankCode.equals(bankCode);
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
}
