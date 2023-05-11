package com.cg.bankapp.dto;

/**
 * A class representing an inter-account transaction like a fund transfer
 * transaction.
 */
public class InterAccountTransactionRequest {

	/** The destination account number. */
	private Long toAccountNo;

	/** The transaction amount */
	private Double amount;

	/**
	 * Returns the transaction amount.
	 * 
	 * @return the transaction amount.
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * Sets the transaction amount.
	 * 
	 * @param amount the transaction amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * Returns the destination account number.
	 * 
	 * @return the destination account number
	 */
	public Long getToAccountNo() {
		return toAccountNo;
	}

	/**
	 * Sets the destination account number.
	 * 
	 * @param toAccountNo the destination account number to set
	 */
	public void setToAccountNo(Long toAccountNo) {
		this.toAccountNo = toAccountNo;
	}

}
