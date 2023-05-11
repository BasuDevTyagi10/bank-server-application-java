package com.cg.bankapp.exceptions;

/**
 * This class is used to throw Exception related to the <code>Account</code>
 * balance.
 */
@SuppressWarnings("serial")
public class AccountBalanceException extends Exception {
	public AccountBalanceException() {
		super("Balance is insufficient.");
	}
}
