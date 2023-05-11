package com.cg.bankapp.exception;

/**
 * This class is used to throw Exception related to <code>Account</code> data
 * not being found in the database.
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
