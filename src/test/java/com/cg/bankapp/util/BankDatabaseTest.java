package com.cg.bankapp.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;

class BankDatabaseTest {
	@Mock
	private Map<String, Account> bankDatabase = BankDatabase.getBankDatabase();

	@Test
	@DisplayName("Check creation of 10 Account objects.")
	void test_AccountCreation() {
		assertAll(() -> {
			assertEquals(10, bankDatabase.size());
		}, () -> {
			for (Entry<String, Account> accountRecord : bankDatabase.entrySet()) {
				assertTrue(accountRecord.getKey().startsWith("180000"));
				assertEquals(accountRecord.getKey(), accountRecord.getValue().getAccountNo());
				assertNotNull(accountRecord.getValue());
				assertInstanceOf(Account.class, accountRecord.getValue());
				assertNotNull(accountRecord.getValue().getCustomer());
				assertInstanceOf(Customer.class, accountRecord.getValue().getCustomer());
			}
		});
	}
}
