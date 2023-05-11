package com.cg.bankapp.dto;

/**
 * A class representing an single-account transaction like a deposit and
 * withdraw transactions.
 */
public class SingleAccountTransactionRequest {

	/** The transaction amount */
	private Double amount;

	/**
	 * Returns the transaction amount.
	 * 
	 * @return the transaction amount
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

}
