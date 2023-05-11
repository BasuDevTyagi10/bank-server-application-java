package com.cg.bankapp.exceptions;

/**
 * This class is used to throw exceptions related to the fund transfer.
 */
@SuppressWarnings("serial")
public class InvalidFundTransferException extends Exception {
	public InvalidFundTransferException(String message) {
		super(message);
	}
}
