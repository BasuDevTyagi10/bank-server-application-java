package com.cg.bankapp.exception;

/**
 * This class is used to throw Exception related to the <code>Transaction</code>
 * amount.
 */
@SuppressWarnings("serial")
public class InvalidAmountException extends BankServerException {
	public InvalidAmountException(String message) {
		super(message);
	}
}
