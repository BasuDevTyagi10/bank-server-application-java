package com.cg.bankapp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.cg.bankapp.util.BankDatabaseConstants;

/**
 * This <code>Transaction</code> class represents a transaction that happens
 * among the <code>Account</code> owned by a <code>Customer</code> in the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
@Entity
@Table(name = "Transaction")
@NamedQuery(name = BankDatabaseConstants.FIND_TRANSACTIONS_BY_ACCOUNT_NO_IDENTIFIER, query = BankDatabaseConstants.FIND_TRANSACTIONS_BY_ACCOUNT_NO_QUERY)
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "txn_id")
	private Long transactionId;
	@Column(name = "txn_type", nullable = false)
	private TransactionType transactionType;
	@Column(name = "txn_datetime", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetime = new Date();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_account")
	private Account fromAccount = null;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_account")
	private Account toAccount = null;

	@Column(name = "txn_amount", nullable = false)
	private Double transactionAmount;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public Date getTransactionDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Account getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}

	public Account getToAccount() {
		return toAccount;
	}

	public void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}

	public Double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Transaction [");
		sb.append("transactionId='").append(transactionId).append("'");
		sb.append(", transactionType='").append(transactionType).append("'");
		sb.append(", datetime='").append(datetime).append("'");
		sb.append(", fromAccount='").append(fromAccount).append("'");
		sb.append(", toAccount='").append(toAccount).append("'");
		sb.append(", transactionAmount='").append(transactionAmount).append("'");
		sb.append("]");
		return sb.toString();
	}

}
