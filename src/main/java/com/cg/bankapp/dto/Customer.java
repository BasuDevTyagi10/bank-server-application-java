package com.cg.bankapp.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * A class representing a bank customer.
 */
public class Customer {

	/** The customer id */
	@ApiModelProperty(hidden = true)
	private Long customerId;

	/** The customer's name */
	private String customerName;

	/**
	 * Returns the customer's id.
	 * 
	 * @return the customer's id
	 */
	public Long getCustomerId() {
		return customerId;
	}

	/**
	 * Sets the customer's id.
	 * 
	 * @param customerId the customer id to set
	 */
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	/**
	 * Returns the customer's name.
	 * 
	 * @return the customer's name
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Sets the customer's name.
	 * 
	 * @param customerName the customer name to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}
