package com.cg.bankapp.dto;

import java.util.List;

import com.cg.bankapp.utils.AccountType;
import com.google.common.base.Objects;

/**
 * A class representing a bank account.
 */
public class Account {

	/** The account number. */
	private Long accountNo;

	/** The account balance. */
	private Double accountBalance;

	/** The type of account. */
	private AccountType accountType;

	/** The customer associated with the account. */
	private Customer customer;

	/** The list of transactions associated with the account. */
	private List<Transaction> transactions;

	/**
	 * Returns the account number.
	 * 
	 * @return the account number
	 */
	public Long getAccountNo() {
		return accountNo;
	}

	/**
	 * Sets the account number.
	 * 
	 * @param accountNo the account number to set
	 */
	public void setAccountNo(Long accountNo) {
		this.accountNo = accountNo;
	}

	/**
	 * Returns the account balance.
	 * 
	 * @return the account balance
	 */
	public Double getAccountBalance() {
		return accountBalance;
	}

	/**
	 * Sets the account balance.
	 * 
	 * @param accountBalance the account balance to set
	 */
	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}

	/**
	 * Returns the type of account.
	 * 
	 * @return the type of account
	 */
	public AccountType getAccountType() {
		return accountType;
	}

	/**
	 * Sets the type of account.
	 * 
	 * @param accountType the type of account to set
	 */
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	/**
	 * Returns the customer associated with the account.
	 * 
	 * @return the customer associated with the account
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Sets the customer associated with the account.
	 * 
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Returns the list of transactions associated with the account.
	 * 
	 * @return the list of transactions associated with the account
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}

	/**
	 * Sets the list of transactions associated with the account.
	 * 
	 * @param transactions the list of transactions to set
	 */
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * Checks if this account is equal to the specified object. Two accounts are
	 * considered equal if their account numbers, account types, and associated
	 * customer ID's are equal.
	 * 
	 * @param obj the object to compare
	 * @return true if the accounts are equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Account)) {
			return false;
		}

		Account account = (Account) obj;

		return Objects.equal(this.accountNo, account.accountNo) && Objects.equal(this.accountType, account.accountType)
				&& Objects.equal(this.customer.getCustomerId(), account.getCustomer().getCustomerId());
	}

	/**
	 * Returns the hash code for this account.
	 * 
	 * @return the hash code for this account
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
