package com.cg.bankapp.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This <code>Account</code> class represents an account entity of the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
@Entity
@Table(name = "Account")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_number")
	private Long accountNo;
	@Column(name = "account_balance", nullable = false, columnDefinition = "FLOAT DEFAULT 0")
	private Double accountBalance = 0.0;
	@Column(name = "account_type", nullable = false, columnDefinition = "INT DEFAULT 0")
	private AccountType accountType = AccountType.SAVINGS;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "transactionId", cascade = CascadeType.ALL)
	private List<Transaction> transactions = new ArrayList<>();

	public Long getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(Long accountNo) {
		this.accountNo = accountNo;
	}

	public Double getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Account [");
		sb.append("accountNo='").append(accountNo).append("'");
		sb.append(", accountBalance='").append(accountBalance).append("'");
		sb.append(", accountType='").append(accountType.name()).append("'");
		sb.append(", customer='").append(customer).append("'");
		sb.append("]");
		return sb.toString();
	}

}