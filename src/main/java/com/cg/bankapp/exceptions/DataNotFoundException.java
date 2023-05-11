package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to data not being found in the
 * database.
 */
@SuppressWarnings("serial")
public class DataNotFoundException extends Exception {

	public DataNotFoundException() {
		this("Data not found.");
	}

	public DataNotFoundException(String message) {
		super(message);
	}

}
