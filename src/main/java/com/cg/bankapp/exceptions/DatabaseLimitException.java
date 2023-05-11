package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the database overflowing.
 */
@SuppressWarnings("serial")
public class DatabaseLimitException extends Exception {

	public DatabaseLimitException() {
		this("Database size full");
	}

	public DatabaseLimitException(String message) {
		super(message);
	}

}
