package com.cg.bankapp.exceptions;

@SuppressWarnings("serial")
public class TransactionUpdationException extends Exception {
	public TransactionUpdationException() {
		super("Failed to update transaction in the account.");
	}
}
