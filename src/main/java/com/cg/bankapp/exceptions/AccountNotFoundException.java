package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to data not being found in the
 * database.
 */
@SuppressWarnings("serial")
public class AccountNotFoundException extends Exception {

	public AccountNotFoundException() {
		this("Account not found.");
	}

	public AccountNotFoundException(String message) {
		super(message);
	}

}
