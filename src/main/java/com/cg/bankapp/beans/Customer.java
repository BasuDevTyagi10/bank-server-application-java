package com.cg.bankapp.beans;

/**
 * This <code>Customer</code> class represents a customer entity of the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Customer {
	private Integer customerId;
	private String customerName;

	public Customer(String customerName) {
		this.customerName = customerName;
	}

	public Customer(Integer customerId, String customerName) {
		this.customerId = customerId;
		this.customerName = customerName;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + "]";
	}

}
