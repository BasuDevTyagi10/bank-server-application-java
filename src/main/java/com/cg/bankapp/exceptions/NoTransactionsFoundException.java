package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the <code>Account</code>
 * transaction if the <code>Account</code> has none.
 */
@SuppressWarnings("serial")
public class NoTransactionsFoundException extends Exception {
	public NoTransactionsFoundException() {
		this("No Transactions found.");
	}

	public NoTransactionsFoundException(String message) {
		super(message);
	}

}
