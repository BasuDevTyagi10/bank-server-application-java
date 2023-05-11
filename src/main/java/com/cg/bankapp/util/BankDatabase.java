package com.cg.bankapp.util;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Customer;
import com.cg.bankapp.util.logging.BankAppLogger;

/**
 * This class is used to populate the database with 10 <code>Account</code>
 * using the <code>EntityManager</code> created in the
 * <code>BankDatabaseEntityManager</code> class.
 */
public class BankDatabase {
	private BankAppLogger logger = new BankAppLogger(getClass());
	private static BankDatabase bankDatabase = null;

	private BankDatabase() {
		EntityManager entityManager = BankDatabaseEntityManager.getInstance();
		List<String> customerNames = Arrays.asList("Basudev Tyagi", "Arjun Upadhyay", "Sonam Sauntiyal",
				"Kartik Singhal", "Anjali Dabral", "Tanishka Rana", "Himanshu Negi", "Garvit Chawla", "Vivek Chamoli",
				"Ashish Karki");
		for (String customerName : customerNames) {
			logger.info("Creating Customer object.");
			Customer customer = new Customer();
			customer.setCustomerName(customerName);

			logger.info("Creating Account object using Customer - " + customer);
			Account account = new Account();
			account.setCustomer(customer);

			logger.info("Saving Account: " + account);
			try {
				entityManager.getTransaction().begin();
				entityManager.persist(account);
				entityManager.getTransaction().commit();
				logger.info("Account saved in database with accountNo = " + account.getAccountNo()
						+ " and  Customer ID = " + customer.getCustomerId());
			} catch (Exception e) {
				logger.error("Error while saving account: " + e.getMessage());
			}
		}
	}

	public static BankDatabase getInstance() {
		if (bankDatabase == null) {
			bankDatabase = new BankDatabase();
		}
		return bankDatabase;
	}

}
