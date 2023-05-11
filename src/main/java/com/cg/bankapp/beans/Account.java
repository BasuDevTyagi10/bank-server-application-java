package com.cg.bankapp.beans;

/**
 * This <code>Account</code> class represents an account entity of the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Account {
	private String accountNo;
	private Double accountBalance;
	private EAccountType accountType;
	private Customer customer;
	private Transaction[] transactions;
	private Integer transactionArrayIndex = 0;

	/**
	 * Constructor to initialize the accountNo, accountBalance and the
	 * <code>Transaction</code> array.
	 */
	private Account() {
		this.accountBalance = 0.0d;
		this.transactions = new Transaction[Transaction.getTxnLimit()];
		this.accountNo = "18000000"
				+ Long.toString((long) (Math.random() * (999999999999l - 100000000000l + 1) + 100000000000l));
	}

	// TODO: remove this constructor in PROD as this is for testing purpose.
	public Account(String accountNo, Customer customer) {
		this();
		this.accountNo = accountNo;
		this.accountType = EAccountType.SAVINGS;
		this.customer = customer;
	}

	/**
	 * Constructor to add a <code>Customer</code> to an <code>Account</code>.
	 * 
	 * @param customer A <code>Customer</code> object to add to
	 *                 <code>Account</code>.
	 */
	public Account(Customer customer) {
		this();
		this.accountType = EAccountType.SAVINGS;
		this.customer = customer;
	}

	/**
	 * Constructor to add a <code>Customer</code> to an <code>Account</code>.
	 * 
	 * @param customer    A <code>Customer</code> object to add to
	 *                    <code>Account</code>.
	 * @param accountType An <code>EAccountType</code> value to set type of
	 *                    <code>Account</code>.
	 */
	public Account(Customer customer, EAccountType accountType) {
		this(customer);
		this.accountType = accountType;
	}

	public Double getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public EAccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(EAccountType accountType) {
		this.accountType = accountType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Transaction[] getTransactions() {
		return transactions;
	}

	public Integer noOfTransactions() {
		return transactionArrayIndex;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public Integer getTransactionArrayIndex() {
		return transactionArrayIndex;
	}

	public void setTransactionArrayIndex(Integer transactionArrayIndex) {
		this.transactionArrayIndex = transactionArrayIndex;
	}

	@Override
	public String toString() {
		return "Account [accountNo=" + accountNo + ", customer = " + customer + "]";
	}

}