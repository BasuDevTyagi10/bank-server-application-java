package com.cg.bankapp.dao;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.util.BankDatabase;

class BankDAOTest {
	IBankDAO bankDAO = new BankDAOImpl();

	@Test
	@DisplayName("Save null Account")
	void testSave_NullAccount_ShouldThrowException() throws InvalidAccountException {
		assertThrows(InvalidAccountException.class, () -> bankDAO.save(null));
	}

	@Test
	@DisplayName("Save Account without Customer")
	void testSave_AccountWithNullCustomer_ShouldThrowException() throws InvalidAccountException {
		Account testAccount = new Account(null);
		assertThrows(InvalidAccountException.class, () -> bankDAO.save(testAccount));
	}

	@Test
	@DisplayName("Save valid Account")
	void testSave_ValidAccount_ShouldReturnTrue() throws InvalidAccountException {
		Account validAccount = new Account("test", new Customer("Test Customer"));
		assertTrue(bankDAO.save(validAccount));
		assertSame(validAccount, BankDatabase.getBankDatabase().get("test"));
	}

	@ParameterizedTest
	@DisplayName("Get Account with invalid account number")
	@ValueSource(strings = { "123456789" })
	@NullSource
	void testGetAccountById_InvalidAccountNumber_ShouldThrowException(String accountNumber)
			throws AccountNotFoundException {
		assertThrows(AccountNotFoundException.class, (() -> bankDAO.getAccountById(accountNumber)));
	}

	@Test
	@DisplayName("Get Account with valid account number")
	void testGetAccountById_ValidAccountNumber_ShouldBeSame() throws AccountNotFoundException, InvalidAccountException {
		Account validAccount = new Account("test", new Customer("Test Customer"));
		bankDAO.save(validAccount);
		assertSame(bankDAO.getAccountById("test"), validAccount);
	}

	@AfterAll
	static void cleanUp() {
		BankDatabase.getBankDatabase().remove("test");
	}

}
