package com.cg.bankapp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;

class TestBankDatabase {
	private Integer originalDatabaseSize = 100;
	private Integer originalDatabaseIndex = 10;
	private Account[] bankDatabase = BankDatabase.DATABASE;

	@Test
	@DisplayName("Check creation of 10 Accounts")
	void test_AccountCreation() {
		test_DatabaseIndexValue();
		int count = 0;
		for (int i = 0; i < BankDatabase.DATABASE_INDEX; i++) {
			Account account = bankDatabase[i];
			test_AccountIsValid(account);
			count++;
		}
		assertEquals(10, count);
	}

	@Test
	@DisplayName("Check value for DATABASE INDEX")
	void test_DatabaseIndexValue() {
		assertTrue(BankDatabase.DATABASE_INDEX == 10);
	}

	@Test
	@DisplayName("Check all Accounts in DATABASE are proper")
	void test_CheckAllAccounts() {
		/*
		 * Check if all accounts in DATABASE are proper i.e. 1. They are a valid and not
		 * null Account object with a valid not null Account Number. 2. They have valid
		 * and not null Customer object attached to them with a valid not null Customer
		 * ID.")
		 */
		test_DatabaseIndexValue();
		for (int i = 0; i < BankDatabase.DATABASE_INDEX; i++) {
			Account account = bankDatabase[i];
			assertNotNull(account);
			assertNotNull(account.getAccountNo());
			if (account.getAccountNo().equals("101") || account.getAccountNo().equals("102")) {
				// TODO: Remove in PROD
				// Dummy Accounts
			} else {
				assertTrue(account.getAccountNo().startsWith("18000000"));
			}
			Customer customer = account.getCustomer();
			assertNotNull(customer);
			assertNotNull(customer.getCustomerId());
			assertTrue(customer.getCustomerId().startsWith("CUST"));
		}
	}

	@Test
	@DisplayName("Check isSpaceAvailable when INDEX < LIMIT")
	void testIsSpaceAvailable_IndexLessThanLimit_ShouldReturnTrue() {
		for (int index = 0; index < originalDatabaseSize; index++) {
			BankDatabase.DATABASE_INDEX = index;
			assertTrue(BankDatabase.isSpaceAvailable());
		}
	}

	@Test
	@DisplayName("Check isSpaceAvailable for Border Cases")
	void testIsSpaceAvailable_IndexMoreThanLimit_ShouldReturnFalse() {
		BankDatabase.DATABASE_INDEX = originalDatabaseSize;
		assertFalse(BankDatabase.isSpaceAvailable());

		BankDatabase.DATABASE_INDEX = originalDatabaseSize + 1;
		assertFalse(BankDatabase.isSpaceAvailable());
	}

	void test_AccountIsValid(Account account) {
		assertNotNull(account);
		assertInstanceOf(Account.class, account);
	}

	@Test
	@DisplayName("Database INDEX is not negative or null")
	void test_DatabaseIndexNotNullOrNegative() {
		assertFalse(BankDatabase.DATABASE_INDEX < 0);
		assertNotNull(BankDatabase.DATABASE_INDEX);
	}

	@AfterEach
	void cleanUp() {
		BankDatabase.DATABASE_INDEX = originalDatabaseIndex;
		BankDatabase.DATABASE_SIZE_LIMIT = originalDatabaseSize;
	}

}
