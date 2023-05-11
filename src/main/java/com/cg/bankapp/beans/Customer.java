package com.cg.bankapp.beans;

/**
 * This <code>Customer</code> class represents a customer entity of the
 * BankServerApplication. It contains all related attributes and getters and
 * setters to get and set these attributes.
 */
public class Customer {
	private String customerId;
	private String customerName = "USER";

	public Customer() {
		this.customerId = "CUST" + (int) (Math.random() * (9999999 - 1000000 + 1) + 1000000);
	}

//	public	Customer(String customerId, String customerName) {
//		this.customerId = customerId;
//		this.customerName = cus
//		
//	}

	public Customer(String customerName) {
		this();
		this.customerName = customerName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + "]";
	}

}
