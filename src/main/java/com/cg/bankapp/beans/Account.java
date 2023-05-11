package com.cg.bankapp.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * This <code>Account</code> class represents an account entity of the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Account {
	private Integer accountNo;
	private Double accountBalance;
	private EAccountType accountType = EAccountType.SAVINGS;

	private Customer customer;
	private List<Transaction> transactions = new ArrayList<>();

	public Account(Customer customer) {
		this.customer = customer;
	}

	public Account(Integer accountNo, Customer customer) {
		this(customer);
		this.accountNo = accountNo;
	}

	public Account(Integer accountNo, Double accountBalance, EAccountType accountType, Customer customer) {
		this(accountNo, customer);
		this.accountBalance = accountBalance;
		this.accountType = accountType;
	}

	public Integer getAccountNo() {
		return accountNo;
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

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public EAccountType getAccountType() {
		return accountType;
	}

	@Override
	public String toString() {
		return "Account [accountNo=" + accountNo + ", accountBalance=" + accountBalance + ", accountType=" + accountType
				+ ", customer = " + customer + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Account)) {
			return false;
		}
		Account account = (Account) obj;
		return this.accountNo.equals(account.accountNo)
				&& this.customer.getCustomerId().equals(account.customer.getCustomerId());
	}

	@Override
	public int hashCode() {
		return accountNo;
	}

}