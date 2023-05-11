package com.cg.bankapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.ETransactionType;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.dao.BankDAOImpl;
import com.cg.bankapp.dao.IBankDAO;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.DatabaseConnectionException;
import com.cg.bankapp.exceptions.DatabaseException;
import com.cg.bankapp.exceptions.DatabaseQueryException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;

class BankServiceTest {
	IBankDAO mockBankDAO = Mockito.mock(BankDAOImpl.class);
	Account mockAccount = Mockito.mock(Account.class);

	@Test
	@DisplayName("Test Constructor")
	void testConstructor() {
		try {
			IBankService bankService = new BankServiceImpl();
			assertNotNull(bankService);
		} catch (DatabaseException e) {
			fail("Constructor threw an exception: " + e.getMessage());
		}
	}

	@ParameterizedTest
	@DisplayName("Show balance for invalid account number")
	@ValueSource(ints = { -1, 0 })
	@NullSource
	void testShowBalance_InvalidAccountNo_ShouldThrowException(Integer id)
			throws DatabaseException, AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(id)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.showBalance(id));
	}

	@Test
	@DisplayName("Show balance for valid account number should return value")
	void testShowBalance_ValidAccountNo_ShouldReturnValue()
			throws AccountNotFoundException, DatabaseException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		assertEquals(100d, bankService.showBalance(101));
	}

	@Test
	@DisplayName("Show balance for valid account but database connection exception")
	void testShowBalance_ValidAccountNoButDatabaseConnectionException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService1 = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService1.showBalance(101));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Show balance for valid account but database query exception")
	void testShowBalance_ValidAccountNoButDatabaseQueryException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseQueryException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.showBalance(101));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@ParameterizedTest
	@DisplayName("Deposit amount in invalid account number")
	@ValueSource(ints = { -1, 0 })
	@NullSource
	void testDeposit_InvalidAccountNo_ShouldThrowException(Integer accountNumber)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.deposit(accountNumber, 20d));
	}

	@Test
	@DisplayName("Deposit amount in valid account but database connection exception")
	void testDeposit_ValidAccountNoButDatabaseConnectionException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService1 = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService1.deposit(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Deposit amount in valid account but database query exception")
	void testDeposit_ValidAccountNoButDatabaseQueryException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseQueryException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.deposit(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Deposit negative or zero amount in a valid account number")
	void testDeposit_NegativeAmount_ShouldThrowException(Double amount)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		assertThrows(InvalidAmountException.class, () -> bankService.deposit(100, amount));
	}

	@Test
	@DisplayName("Deposit amount in valid account but transaction update fails due to connection")
	void testDeposit_ValidAmountTransactionFails_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.any(Transaction.class)))
				.thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.deposit(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Deposit amount in valid account but transaction update fails due to query")
	void testDeposit_ValidAmountTransactionQueryFails_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.any(Transaction.class))).thenThrow(DatabaseQueryException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.deposit(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Deposit amount in valid account")
	void testDeposit_ValidAmount_ShouldReturnValue() throws AccountNotFoundException, InvalidAccountException,
			DatabaseConnectionException, DatabaseQueryException, InvalidAmountException, DatabaseException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class))).thenReturn(1);
		when(mockBankDAO.updateBalance(Mockito.notNull(Account.class))).thenReturn(120d);

		assertEquals(120d, bankService.deposit(101, 20d));
	}

	@Test
	@DisplayName("Deposit amount in account turning null during update")
	void testDeposit_NullAmount_ShouldThrowException() throws AccountNotFoundException, InvalidAccountException,
			DatabaseConnectionException, DatabaseQueryException, InvalidAmountException, DatabaseException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class))).thenReturn(1);
		when(mockBankDAO.updateBalance(Mockito.notNull(Account.class))).thenThrow(InvalidAccountException.class);

		assertThrows(InvalidAccountException.class, () -> bankService.deposit(101, 20d));
	}

	@ParameterizedTest
	@DisplayName("Withdraw amount from invalid account number")
	@ValueSource(ints = { -1, 0 })
	@NullSource
	void testWithdraw_InvalidAccountNo_ShouldThrowException(Integer accountNumber)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.withdraw(accountNumber, 20d));
	}

	@Test
	@DisplayName("Withdraw amount from valid account but database connection exception")
	void testWithdraw_ValidAccountNoButDatabaseConnectionException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService1 = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService1.withdraw(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Withdraw amount from valid account but database query exception")
	void testWithdraw_ValidAccountNoButDatabaseQueryException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseQueryException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.withdraw(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Withdraw negative or zero amount from a valid account number")
	void testWithdraw_NegativeAmount_ShouldThrowException(Double amount)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		assertThrows(InvalidAmountException.class, () -> bankService.withdraw(100, amount));
	}

	@Test
	@DisplayName("Withdraw amount from valid account but transaction update fails due to connection")
	void testWithdraw_ValidAmountTransactionFails_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.any(Transaction.class)))
				.thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.withdraw(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Withdraw amount from valid account but transaction update fails due to query")
	void testWithdraw_ValidAmountTransactionQueryFails_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.any(Transaction.class))).thenThrow(DatabaseQueryException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.withdraw(101, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Withdraw amount from valid account but less balance.")
	void testWithdraw_ValidAmountWithLessBalance_ShouldThrowException()
			throws AccountNotFoundException, InvalidAccountException, DatabaseConnectionException,
			DatabaseQueryException, InvalidAmountException, DatabaseException, AccountBalanceException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(10d);

		assertThrows(AccountBalanceException.class, () -> bankService.withdraw(101, 20d));
	}

	@Test
	@DisplayName("Withdraw amount from valid account")
	void testWithdraw_ValidAmount_ShouldReturnValue()
			throws AccountNotFoundException, InvalidAccountException, DatabaseConnectionException,
			DatabaseQueryException, InvalidAmountException, DatabaseException, AccountBalanceException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class))).thenReturn(1);
		when(mockBankDAO.updateBalance(Mockito.notNull(Account.class))).thenReturn(80d);

		assertEquals(80d, bankService.withdraw(101, 20d));
	}

	@Test
	@DisplayName("Withdraw amount in account turning null during update")
	void testWithdraw_NullAmount_ShouldThrowException() throws AccountNotFoundException, InvalidAccountException,
			DatabaseConnectionException, DatabaseQueryException, InvalidAmountException, DatabaseException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class))).thenReturn(1);
		when(mockBankDAO.updateBalance(Mockito.any(Account.class))).thenThrow(InvalidAccountException.class);

		assertThrows(InvalidAccountException.class, () -> bankService.withdraw(101, 20d));
	}

	@ParameterizedTest
	@DisplayName("Transfer from invalid account number")
	@ValueSource(ints = { -1, 0 })
	@NullSource
	void testFundTransfer_InvalidFromAccountNo_ShouldThrowException(Integer accountNumber)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer(accountNumber, 101, 20d));
	}

	@ParameterizedTest
	@DisplayName("Transfer to invalid account number")
	@ValueSource(ints = { -1, 0 })
	@NullSource
	void testFundTransfer_InvalidToAccountNo_ShouldThrowException(Integer accountNumber)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer(101, accountNumber, 20d));
	}

	@Test
	@DisplayName("Transfer to same account number")
	void testFundTransfer_SameAccount_ShouldThrowException() {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		assertThrows(InvalidFundTransferException.class, () -> bankService.fundTransfer(101, 101, 100d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Transfer negative or zero balance should give apt error.")
	void testFundTransfer_NegativeAmount_ShouldThrowException(Double amount) {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		assertThrows(InvalidAmountException.class, () -> bankService.fundTransfer(101, 102, amount));
	}

	@Test
	@DisplayName("Transfer amount from valid account but database connection exception")
	void testFundTransfer_ValidAccountNoButDatabaseConnectionException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService1 = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseConnectionException.class);
		when(mockBankDAO.getAccountById(102)).thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService1.fundTransfer(101, 102, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Transfer amount from valid account but database query exception")
	void testFundTrasnfer_ValidAccountNoButDatabaseQueryException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseQueryException.class);
		when(mockBankDAO.getAccountById(102)).thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.fundTransfer(101, 102, 20d));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Transfer from valid amount with low balance")
	void testFundTransfer_FromValidAccountNoWithLowBalance_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);

		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockBankDAO.getAccountById(102)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(10d);

		assertThrows(AccountBalanceException.class, () -> bankService.fundTransfer(101, 102, 100d));
	}

	@Test
	@DisplayName("Valid Fund Transfer Transaction")
	void testFundTransfer_ValidCase_ShouldReturnTrue() throws InvalidFundTransferException, AccountNotFoundException,
			AccountBalanceException, InvalidAmountException, DatabaseException, InvalidAccountException,
			DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);

		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockBankDAO.getAccountById(102)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockAccount.getAccountBalance()).thenReturn(200d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class))).thenReturn(1);
		when(mockBankDAO.updateBalance(Mockito.notNull(Account.class))).thenReturn(80d);
		when(mockBankDAO.updateBalance(Mockito.notNull(Account.class))).thenReturn(220d);

		assertTrue(bankService.fundTransfer(101, 102, 20d));
	}

	@Test
	@DisplayName("Fund Transfer Transaction Database Connection Fails")
	void testFundTransfer_DatabaseConnectionFails_ShouldThrowException() throws InvalidFundTransferException,
			AccountNotFoundException, AccountBalanceException, InvalidAmountException, DatabaseException,
			InvalidAccountException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);

		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockBankDAO.getAccountById(102)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockAccount.getAccountBalance()).thenReturn(200d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class)))
				.thenThrow(DatabaseConnectionException.class);

		assertThrows(DatabaseException.class, () -> bankService.fundTransfer(101, 102, 100d));
	}

	@Test
	@DisplayName("Fund Transfer Transaction Query Fails")
	void testFundTransfer_DatabaseQueryFails_ShouldThrowException() throws InvalidFundTransferException,
			AccountNotFoundException, AccountBalanceException, InvalidAmountException, DatabaseException,
			InvalidAccountException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);

		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockBankDAO.getAccountById(102)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockAccount.getAccountBalance()).thenReturn(200d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class))).thenThrow(DatabaseQueryException.class);

		assertThrows(DatabaseException.class, () -> bankService.fundTransfer(101, 102, 100d));
	}

	@Test
	@DisplayName("Transfer amount in account turning null during update")
	void testFundTransfer_NullAmount_ShouldThrowException() throws AccountNotFoundException, InvalidAccountException,
			DatabaseConnectionException, DatabaseQueryException, InvalidAmountException, DatabaseException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockBankDAO.getAccountById(102)).thenReturn(mockAccount);
		when(mockAccount.getAccountBalance()).thenReturn(100d);
		when(mockAccount.getAccountBalance()).thenReturn(200d);
		when(mockBankDAO.createTransaction(Mockito.notNull(Transaction.class))).thenReturn(1);
		when(mockBankDAO.updateBalance(Mockito.any(Account.class))).thenThrow(InvalidAccountException.class);

		assertThrows(InvalidAccountException.class, () -> bankService.fundTransfer(101, 102, 20d));
	}

	@ParameterizedTest
	@DisplayName("Get All Transactions for invalid account number")
	@ValueSource(ints = { -1, 0 })
	@NullSource
	void testGetAllTransactions_InvalidAccountNo_ShouldThrowException(Integer id)
			throws DatabaseException, AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(id)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.getAllTransactionDetails(id));
	}

	@Test
	@DisplayName("Get All Transactions for valid account number should return 10 transactions")
	void testGetAllTransactions_ValidAccountNo_ShouldReturnList() throws AccountNotFoundException, DatabaseException,
			DatabaseConnectionException, DatabaseQueryException, NoTransactionsFoundException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);

		List<Transaction> dummyTransactions = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			Transaction transaction = new Transaction(i, ETransactionType.CREDIT, Timestamp.from(Instant.now()), null,
					101, 10d);
			dummyTransactions.add(transaction);
		}

		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getTransactions()).thenReturn(dummyTransactions);
		assertEquals(10, bankService.getAllTransactionDetails(101).size());
	}

	@Test
	@DisplayName("Get All Transactions for valid account number should throw No Transactions Found")
	void testGetAllTransactions_ValidAccountNo_ShouldThrowException() throws AccountNotFoundException,
			DatabaseException, DatabaseConnectionException, DatabaseQueryException, NoTransactionsFoundException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);

		List<Transaction> dummyTransactions = new ArrayList<>();

		when(mockBankDAO.getAccountById(101)).thenReturn(mockAccount);
		when(mockAccount.getTransactions()).thenReturn(dummyTransactions);
		assertThrows(NoTransactionsFoundException.class, () -> bankService.getAllTransactionDetails(101));
	}

	@Test
	@DisplayName("Get All Transactions for valid account but database connection exception")
	void testGetAllTransactions_ValidAccountNoButDatabaseConnectionException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService1 = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseConnectionException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService1.getAllTransactionDetails(101));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}

	@Test
	@DisplayName("Get All Transactions for valid account but database query exception")
	void testGetAllTransactions_ValidAccountNoButDatabaseQueryException_ShouldThrowException()
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		IBankService bankService = new BankServiceImpl(mockBankDAO);
		when(mockBankDAO.getAccountById(101)).thenThrow(DatabaseQueryException.class);
		Exception exception = assertThrows(DatabaseException.class, () -> bankService.getAllTransactionDetails(101));
		assertTrue(exception.getMessage().startsWith("DATABASE ERROR: "));
	}
}
