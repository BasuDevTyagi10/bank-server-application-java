package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the connection to
 * <code>BankDatabase</code>.
 */
@SuppressWarnings("serial")
public class DatabaseConnectionException extends Exception {

	public DatabaseConnectionException() {
		this("Something went wrong while connecting to the database.");
	}

	public DatabaseConnectionException(String message) {
		super(message);
	}

}
