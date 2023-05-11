package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to <code>Transaction</code>
 * limit.
 */
@SuppressWarnings("serial")
public class TransactionLimitReachedException extends Exception {

	public TransactionLimitReachedException() {
		this("Cannot commit transaction. Account has reached transaction limit");
	}

	public TransactionLimitReachedException(String message) {
		super(message);
	}

}
