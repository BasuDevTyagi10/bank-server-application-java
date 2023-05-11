package com.cg.bankapp.dao;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;
import com.cg.bankapp.exceptions.DataNotFoundException;
import com.cg.bankapp.exceptions.DatabaseLimitException;
import com.cg.bankapp.util.BankDatabase;

class TestBankDAO {
	private Integer originalDatabaseSize = 100;
	private Integer originalDatabaseIndex = 10;
	private IBankDAO bankDAO = new BankDAOImpl();

	@Test
	@DisplayName("Saving null Account")
	void testSave_NullAccount_ShouldThrowException() throws IllegalArgumentException {
		assertThrows(IllegalArgumentException.class, (() -> bankDAO.save(null)));
	}

	@Test
	@DisplayName("Saving Account after Database limit")
	void testSave_DatabaseLimit_ShouldThrowException() throws DatabaseLimitException {
		BankDatabase.DATABASE_SIZE_LIMIT = 10;
		assertThrows(DatabaseLimitException.class, (() -> bankDAO.save(new Account(new Customer()))));
	}

	@Test
	@DisplayName("Saving a valid Account")
	void testSave_ValidAccountNo_ShouldReturnTrue() throws DatabaseLimitException {
		assertTrue(bankDAO.save(new Account(new Customer())));
	}

	@Test
	@DisplayName("Getting Account with invalid Account number")
	void testGetAccountById_InvalidAccountNumber_ShouldThrowException() throws DataNotFoundException {
		assertThrows(DataNotFoundException.class, (() -> bankDAO.getAccountById("103")));
	}

	@Test
	@DisplayName("Getting Account with valid Account number")
	void testGetAccountById_ValidAccountNumber_ShouldBeSame() throws DataNotFoundException {
		assertSame(bankDAO.getAccountById("101"), BankDatabase.DATABASE[0]);
	}

	@AfterEach
	void cleanUp() {
		BankDatabase.DATABASE_SIZE_LIMIT = originalDatabaseSize;
		BankDatabase.DATABASE_INDEX = originalDatabaseIndex;
		BankDatabase.DATABASE[10] = null;
	}

}
