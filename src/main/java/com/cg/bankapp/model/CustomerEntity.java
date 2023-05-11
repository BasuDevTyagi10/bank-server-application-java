package com.cg.bankapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This <code>Customer</code> class represents a customer JPA entity for the
 * database. It contains all related attributes and getters and setters to get
 * and set these attributes.
 */
@Entity
@Table(name = "Customer")
public class CustomerEntity {

	/** The customer id. This is an auto-generated primary key. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Long customerId;

	/** The customer name */
	@Column(name = "customer_name")
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
