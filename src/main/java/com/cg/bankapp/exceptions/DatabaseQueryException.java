package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the query to
 * <code>BankDatabase</code>.
 */
@SuppressWarnings("serial")
public class DatabaseQueryException extends Exception {

	public DatabaseQueryException() {
		this("Invalid SQL Query! Only DML and DDL statments are allowed.");
	}

	public DatabaseQueryException(String message) {
		super(message);
	}

}
