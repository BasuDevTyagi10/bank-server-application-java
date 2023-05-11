package com.cg.bankapp.exception;

/**
 * This class is used to throw Exception related to the <code>Account</code>
 * balance like balance being insufficient.
 */
@SuppressWarnings("serial")
public class AccountBalanceException extends BankServerException {
	public AccountBalanceException() {
		super("Balance is insufficient.");
	}
}
