package com.cg.bankapp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.cg.bankapp.utils.TransactionType;

/**
 * This <code>Transaction</code> class represents a transaction JPA entity that
 * happens among the <code>Account</code> owned by a <code>Customer</code> in
 * the BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
@Entity
@Table(name = "Transaction")
public class TransactionEntity {

	/** The transaction id. This is an auto-generated primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "txn_id")
	private Long transactionId;

	/** The type of transaction. This cannot be null. */
	@Column(name = "txn_type", nullable = false)
	private TransactionType transactionType;

	/** The datetime of the transaction. Defaults to current datetime */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "txn_datetime")
	private Date datetime = new Date();

	/** The source account entity */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "from_account")
	private AccountEntity fromAccount = null;

	/** The destination account entity */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "to_account")
	private AccountEntity toAccount = null;

	/** The transaction amount */
	@Column(name = "txn_amount", nullable = false)
	private Double transactionAmount;

	/**
	 * Returns the transaction id.
	 * 
	 * @return the transaction id
	 */
	public Long getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 * 
	 * @param transactionId the transaction id to set.
	 */
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * Returns the transaction type.
	 * 
	 * @return the transaction type
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * Sets the transaction type.
	 * 
	 * @param transactionType the transaction type to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * Returns the transaction datetime.
	 * 
	 * @return the transaction datetime
	 */
	public Date getDatetime() {
		return datetime;
	}

	/**
	 * Sets the transaction datetime.
	 * 
	 * @param datetime the datetime to set
	 */
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	/**
	 * Returns the source account number.
	 * 
	 * @return the source account number
	 */
	public AccountEntity getFromAccount() {
		return fromAccount;
	}

	/**
	 * Sets the source account number.
	 * 
	 * @param fromAccount the source account number to set.
	 */
	public void setFromAccount(AccountEntity fromAccount) {
		this.fromAccount = fromAccount;
	}

	/**
	 * Returns the destination account number.
	 * 
	 * @return the destination account number
	 */
	public AccountEntity getToAccount() {
		return toAccount;
	}

	/**
	 * Sets the destination account number.
	 * 
	 * @param toAccount the destination account number to set
	 */
	public void setToAccount(AccountEntity toAccount) {
		this.toAccount = toAccount;
	}

	/**
	 * Returns the transaction amount.
	 * 
	 * @return the transaction amount
	 */
	public Double getTransactionAmount() {
		return transactionAmount;
	}

	/**
	 * Sets the transaction amount.
	 * 
	 * @param transactionAmount the transaction amount to set
	 */
	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

}
