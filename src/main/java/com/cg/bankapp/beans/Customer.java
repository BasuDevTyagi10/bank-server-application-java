package com.cg.bankapp.beans;

import java.util.Random;

/**
 * This <code>Customer</code> class represents a customer entity of the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Customer {
	private String customerId;
	private String customerName = "USER";

	public Customer() {
		this.customerId = "CUST" + Math.abs(1000000 + (new Random().nextInt() * (9999999 - 1000000)));
	}

	public Customer(String customerName) {
		this();
		this.customerName = customerName;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + "]";
	}

}
