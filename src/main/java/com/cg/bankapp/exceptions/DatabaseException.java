package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the
 * <code>BankDatabase</code>.
 */
@SuppressWarnings("serial")
public class DatabaseException extends Exception {
	public DatabaseException(String reason) {
		super("DATABASE ERROR: " + reason);
	}
}
