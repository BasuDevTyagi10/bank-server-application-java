package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the <code>Account</code>
 * balance.
 */
@SuppressWarnings("serial")
public class AccountBalanceException extends Exception {

	public AccountBalanceException() {
		this("Balance is insufficient.");
	}

	public AccountBalanceException(String message) {
		super(message);
	}

}
