package com.cg.bankapp.beans;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

/**
 * This <code>Transaction</code> class represents a transaction that happens
 * among the <code>Account</code> for various <code>Customer</code> in the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Transaction {
	private String transactionId;
	private ETransactionType transactionType;
	private Timestamp datetime;

	private String fromAccount;
	private String toAccount;
	private Double transactionAmount;

	public Transaction() {
		this.transactionId = "TXN-" + Math.abs(10000000 + (new Random().nextInt() * (99999999 - 10000000)));
		this.datetime = Timestamp.from(Instant.now());
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

	public Timestamp getTransactionDatetime() {
		return datetime;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public String getToAccount() {
		return toAccount;
	}

	public Double getTransactionAmount() {
		return transactionAmount;
	}

	@Override
	public String toString() {
		return "transactionId=" + transactionId + ", transactionType=" + transactionType + ", transactionDatetime="
				+ datetime;
	}

}
