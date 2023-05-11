package com.cg.bankapp.exception;

/**
 * This class is used to throw Exception related to the <code>Transaction</code>
 * failing due to some error.
 */
@SuppressWarnings("serial")
public class TransactionFailedException extends BankServerException {
	public TransactionFailedException(String reason) {
		super("Transaction Failed! Reason: " + reason);
	}
}
