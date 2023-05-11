package com.cg.bankapp.beans;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * This <code>Transaction</code> class represents a transaction that happens
 * among the <code>Account</code> for various <code>Customer</code> in the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Transaction {
	private static Integer TXN_LIMIT = 10;
	private String transactionId;
	private ETransactionType transactionType;
	private Timestamp transactionDatetime;

	private String fromAccount;
	private String toAccount;
	private Double transactionAmount;

	public Transaction() {
		this.transactionId = "TXN-" + (int) (Math.random() * (99999999 - 10000000 + 1) + 10000000);
		this.transactionDatetime = Timestamp.from(Instant.now());
	}

	public Transaction(ETransactionType transactionType) {
		this();
		this.transactionType = transactionType;
	}

	public Transaction(ETransactionType transactionType, String fromAccount, String toAccount,
			Double transactionAmount) {
		this(transactionType);
		this.fromAccount = fromAccount != null ? fromAccount : "-";
		this.toAccount = toAccount != null ? toAccount : "-";
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public ETransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(ETransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public Timestamp getTransactionDatetime() {
		return transactionDatetime;
	}

	public void setTransactionDatetime(Timestamp transactionDatetime) {
		this.transactionDatetime = transactionDatetime;
	}

	public static Integer getTxnLimit() {
		return TXN_LIMIT;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
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
		return "transactionId=" + transactionId + ", transactionType=" + transactionType + ", transactionDatetime="
				+ transactionDatetime;
	}

}
