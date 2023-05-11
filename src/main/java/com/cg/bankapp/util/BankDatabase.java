package com.cg.bankapp.util;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;

/**
 * This class holds the database of our BankServerApplication as an array of
 * <code>Account</code> objects.
 */

public class BankDatabase {
	public static Integer DATABASE_SIZE_LIMIT = 100;
	public static Integer DATABASE_INDEX = 0;
	public static Account[] DATABASE = new Account[DATABASE_SIZE_LIMIT];

	static {
		/*
		 * Static initialization block to fill dummy data in the database, for testing
		 * purpose.
		 */
		System.out.println("ADDING DUMMY DATA...");
		String[] customerNames = { "Basudev Tyagi", "Arjun Upadhyay", "Sonam Sauntiyal", "Kartik Singhal",
				"Anjali Dabral", "Tanishka Rana", "Himanshu Negi", "Garvit Chawla", "Vivek Chamoli", "Ashish Karki", };
		for (int i = 0; i < customerNames.length; i++) {
			Customer customer = new Customer(customerNames[i]);
			Account account = null;
			if (customerNames[i].equals("Basudev Tyagi")) {
				// TODO: Remove in PROD
				// To fill manual accountNo for some users to test our application.
				account = new Account("101", customer);
			} else if (customerNames[i].equals("Arjun Upadhyay")) {
				// TODO: Remove in PROD
				// To fill manual accountNo for some users to test our application.
				account = new Account("102", customer);
			} else {
				account = new Account(customer);
			}
			DATABASE[DATABASE_INDEX++] = account;
			System.out.println("Added -> " + account);
		}
		System.out.println("DUMMY DATA ADDED SUCESSFULLY\n");
	}

	/**
	 * Check if space is available in the database.
	 * 
	 * @param index the database array is currently at.
	 * @return true if space is available else false.
	 */
	public static Boolean isSpaceAvailable() {
		return DATABASE_INDEX < DATABASE_SIZE_LIMIT;
	}
}
