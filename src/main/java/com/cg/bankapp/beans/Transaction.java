package com.cg.bankapp.beans;

import java.sql.Timestamp;

/**
 * This <code>Transaction</code> class represents a transaction that happens
 * among the <code>Account</code> for various <code>Customer</code> in the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Transaction {
	private Integer transactionId;
	private ETransactionType transactionType;
	private Timestamp datetime;

	private Integer fromAccount;
	private Integer toAccount;
	private Double transactionAmount;

	public Transaction(ETransactionType transactionType, Integer fromAccount, Integer toAccount,
			Double transactionAmount) {
		this.transactionType = transactionType;
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.transactionAmount = transactionAmount;
	}

	public Transaction(Integer transactionId, ETransactionType transactionType, Timestamp datetime, Integer fromAccount,
			Integer toAccount, Double transactionAmount) {
		this(transactionType, fromAccount, toAccount, transactionAmount);
		this.transactionId = transactionId;
		this.datetime = datetime;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public ETransactionType getTransactionType() {
		return transactionType;
	}

	public Timestamp getTransactionDatetime() {
		return datetime;
	}

	public Integer getFromAccount() {
		return fromAccount;
	}

	public Integer getToAccount() {
		return toAccount;
	}

	public Double getTransactionAmount() {
		return transactionAmount;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", transactionType=" + transactionType + ", datetime="
				+ datetime + ", fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", transactionAmount="
				+ transactionAmount + "]";
	}

}
