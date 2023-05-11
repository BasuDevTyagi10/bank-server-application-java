package com.cg.bankapp.dto;

import java.util.Date;

import com.cg.bankapp.utils.TransactionType;
import com.google.common.base.Objects;

/**
 * A class representing a bank transaction.
 */
public class Transaction {

	/** The transaction id */
	private Long transactionId;

	/** The type of transaction */
	private TransactionType transactionType;

	/** The datetime for the transaction */
	private Date datetime;

	/** The account number from which transaction begins */
	private Long fromAccount;

	/** The account number to which transaction ends */
	private Long toAccount;

	/** The transaction amount */
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
	public Long getFromAccount() {
		return fromAccount;
	}

	/**
	 * Sets the source account number.
	 * 
	 * @param fromAccount the source account number to set.
	 */
	public void setFromAccount(Long fromAccount) {
		this.fromAccount = fromAccount;
	}

	/**
	 * Returns the destination account number.
	 * 
	 * @return the destination account number
	 */
	public Long getToAccount() {
		return toAccount;
	}

	/**
	 * Sets the destination account number.
	 * 
	 * @param toAccount the destination account number to set
	 */
	public void setToAccount(Long toAccount) {
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

	/**
	 * Checks if this transaction is equal to the specified object. Two transactions
	 * are considered equal if their transaction id, transaction amount, transaction
	 * datetime, transaction type, source account and destination account are equal.
	 * 
	 * @param obj the object to compare
	 * @return true is transactions are equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Transaction)) {
			return false;
		}

		Transaction transaction = (Transaction) obj;

		return Objects.equal(this.transactionId, transaction.transactionId)
				&& Objects.equal(this.fromAccount, transaction.fromAccount)
				&& Objects.equal(this.toAccount, transaction.toAccount)
				&& Objects.equal(this.transactionAmount, transaction.transactionAmount)
				&& Objects.equal(this.datetime, transaction.datetime)
				&& Objects.equal(this.transactionType, transaction.transactionType);
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
