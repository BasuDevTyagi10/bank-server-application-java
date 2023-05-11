package com.cg.bankapp.exception;

/**
 * This class is used to throw Exception related to an invalid
 * <code>Account</code> object.
 */
@SuppressWarnings("serial")
public class InvalidAccountException extends Exception {
	public InvalidAccountException(String message) {
		super(message);
	}
}
