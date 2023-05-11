package com.cg.bankapp.exception;

/**
 * This class is used to throw Exception related to the <code>Account</code>
 * transaction if the <code>Account</code> has none.
 */
@SuppressWarnings("serial")
public class NoTransactionsFoundException extends Exception {
	public NoTransactionsFoundException() {
		super("No Transactions found.");
	}
}
