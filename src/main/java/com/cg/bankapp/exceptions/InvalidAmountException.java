package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the
 * <code>Transaction</code> amount
 */
@SuppressWarnings("serial")
public class InvalidAmountException extends Exception {
	public InvalidAmountException(String message) {
		super(message);
	}
}
