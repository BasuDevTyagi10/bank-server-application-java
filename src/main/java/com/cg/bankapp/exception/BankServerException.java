package com.cg.bankapp.exception;

/**
 * This class is the super class for all the exceptions related to the Bank
 * Server Application.
 */

@SuppressWarnings("serial")
public class BankServerException extends RuntimeException {
	public BankServerException(String errorMessage) {
		super(errorMessage);
	}
}
