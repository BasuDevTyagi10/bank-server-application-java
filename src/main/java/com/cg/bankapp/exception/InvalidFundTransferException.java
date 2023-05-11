package com.cg.bankapp.exception;

/**
 * This class is used to throw Exception related to the fund transfer
 * <code>Transaction</code> being invalid.
 */
@SuppressWarnings("serial")
public class InvalidFundTransferException extends BankServerException {
	public InvalidFundTransferException(String message) {
		super(message);
	}
}
