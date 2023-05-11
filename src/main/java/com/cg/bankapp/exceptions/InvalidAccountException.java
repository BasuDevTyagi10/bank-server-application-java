package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to an invalid
 * <code>Account</code> object.
 */
@SuppressWarnings("serial")
public class InvalidAccountException extends Exception {
	public InvalidAccountException(String message) {
		super(message);
	}
}
