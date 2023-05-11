package com.cg.bankapp.exceptions;

@SuppressWarnings("serial")
public class NoTransactionsFoundException extends Exception {
	public NoTransactionsFoundException() {
		this("No Transactions found.");
	}

	public NoTransactionsFoundException(String message) {
		super(message);

	}

}
