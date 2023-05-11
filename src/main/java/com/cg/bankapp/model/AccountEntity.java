package com.cg.bankapp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.cg.bankapp.utils.AccountType;

/**
 * This <code>Account</code> class represents an account JPA entity for the
 * database. It contains all related attributes and getters and setters to get
 * and set these attributes.
 */
@Entity
@Table(name = "Account")
public class AccountEntity {

	/** The account number. This is an auto-generated primary key. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_number")
	private Long accountNo;

	/** The account balance. This defaults to 0. */
	@Column(name = "account_balance", columnDefinition = "FLOAT DEFAULT 0")
	private Double accountBalance = 0.0;

	/** The type of account. This defaults to 0 i.e. AccountType.SAVINGS account. */
	@Column(name = "account_type", columnDefinition = "INT DEFAULT 0")
	private AccountType accountType = AccountType.SAVINGS;

	/** The CustomerEntity associated with the account. */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id")
	private CustomerEntity customer;

	/** The list of TransactionEntity associated with the account. */
	@OneToMany(mappedBy = "transactionId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<TransactionEntity> transactions = new ArrayList<>();

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
	public CustomerEntity getCustomer() {
		return customer;
	}

	/**
	 * Sets the customer associated with the account.
	 * 
	 * @param customer the customer to set
	 */
	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	/**
	 * Returns the list of transactions associated with the account.
	 * 
	 * @return the list of transactions associated with the account
	 */
	public List<TransactionEntity> getTransactions() {
		return transactions;
	}

	/**
	 * Sets the list of transactions associated with the account.
	 * 
	 * @param transactions the list of transactions to set
	 */
	public void setTransactions(List<TransactionEntity> transactions) {
		this.transactions = transactions;
	}

	/**
	 * Add a new transaction to the list of transactions associated with the
	 * account.
	 * 
	 * @param transaction the new transaction object
	 */
	public void addNewTransaction(TransactionEntity transaction) {
		transactions.add(transaction);
	}

}