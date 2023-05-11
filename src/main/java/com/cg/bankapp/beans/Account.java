package com.cg.bankapp.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	private List<Transaction> transactions = new ArrayList<>();

	/**
	 * Constructor to initialize the accountNo, accountBalance and the
	 * <code>Transaction</code> array.
	 */
	private Account() {
		this.accountBalance = 0.0d;
		this.accountNo = "180000"
				+ Math.abs(100000000000L + (new Random().nextLong() * (999999999999L - 100000000000L)));

	}

	// Used for testing
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

	public Double getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public String getAccountNo() {
		return accountNo;
	}

	@Override
	public String toString() {
		return "Account [accountNo=" + accountNo + ", accountType=" + accountType + ", customer = " + customer + "]";
	}

}