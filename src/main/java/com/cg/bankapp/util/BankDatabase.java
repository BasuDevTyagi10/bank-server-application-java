package com.cg.bankapp.util;

import java.util.HashMap;
import java.util.Map;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;

/**
 * This class holds the database of our BankServerApplication as an array of
 * <code>Account</code> objects.
 */

public class BankDatabase {
	private static Map<String, Account> database = new HashMap<>();

	public static Map<String, Account> getBankDatabase() {
		return database;
	}

	private BankDatabase() {

	}

	static {
		/*
		 * Static initialization block to fill dummy data in the database, for testing
		 * purpose.
		 */
		System.out.println("ADDING DUMMY DATA...");
		String[] customerNames = { "Basudev Tyagi", "Arjun Upadhyay", "Sonam Sauntiyal", "Kartik Singhal",
				"Anjali Dabral", "Tanishka Rana", "Himanshu Negi", "Garvit Chawla", "Vivek Chamoli", "Ashish Karki", };
		for (String customerName : customerNames) {
			Customer customer = new Customer(customerName);
			Account account = null;
			account = new Account(customer);
			database.put(account.getAccountNo(), account);
			System.out.println("Added -> " + account);
		}
		System.out.println("DUMMY DATA ADDED SUCESSFULLY\n");
	}

}
