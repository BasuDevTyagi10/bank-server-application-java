package com.cg.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.cg.bankapp.dao.BankDAO;
import com.cg.bankapp.dao.BankDAOImpl;
import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Customer;
import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.entity.TransactionType;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.exceptions.TransactionFailedException;

class BankServiceTest {
	BankService bankService;
	BankDAO mockBankDAO;

	@BeforeEach
	void setUp() {
		mockBankDAO = mock(BankDAOImpl.class);
		bankService = new BankServiceImpl(mockBankDAO);
	}

	@Test
	@DisplayName("Create Account")
	void testCreateAccount_ValidCustomer_ShouldReturnValue()
			throws InvalidAccountException, TransactionFailedException {
		when(mockBankDAO.save(any(Account.class))).thenReturn(1L);

		assertEquals(1L, bankService.createAccount("Test"));
	}

	@ParameterizedTest
	@DisplayName("Show balance for invalid account number")
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testShowBalance_InvalidAccountNo_ShouldThrowException(Long id)
			throws AccountNotFoundException, TransactionFailedException {
		when(mockBankDAO.getAccountById(id)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.showBalance(id));
	}

	@Test
	@DisplayName("Show balance for valid account number should return value")
	void testShowBalance_ValidAccountNo_ShouldReturnValue()
			throws AccountNotFoundException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validAccount = new Account();
		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);
		validAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);

		assertEquals(100d, bankService.showBalance(101L));
	}

	@ParameterizedTest
	@DisplayName("Deposit amount in invalid account number")
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testDeposit_InvalidAccountNo_ShouldThrowException(Long accountNumber)
			throws AccountNotFoundException, TransactionFailedException {
		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);

		assertThrows(AccountNotFoundException.class, () -> bankService.deposit(accountNumber, 20d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Deposit negative or zero amount in a valid account number")
	void testDeposit_NegativeAmount_ShouldThrowException(Double amount)
			throws AccountNotFoundException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validAccount = new Account();
		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);
		validAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);

		assertThrows(InvalidAmountException.class, () -> bankService.deposit(100L, amount));
	}

	@Test
	@DisplayName("Deposit amount in valid account")
	void testDeposit_ValidAmount_ShouldReturnValue() throws AccountNotFoundException, InvalidAccountException,
			InvalidAmountException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validAccount = new Account();
		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);
		validAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);

		doAnswer(invocation -> {
			Account account = invocation.getArgument(0);
			Double newBalance = account.getAccountBalance() + (Double) invocation.getArgument(2);
			account.setAccountBalance(newBalance);
			return 12345L;
		}).when(mockBankDAO).performTransaction(validAccount, TransactionType.CREDIT, 50.0);

		assertEquals(150.0, bankService.deposit(101L, 50d));
	}

	@ParameterizedTest
	@DisplayName("Withdraw amount from invalid account number")
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testWithdraw_InvalidAccountNo_ShouldThrowException(Long accountNumber)
			throws AccountNotFoundException, TransactionFailedException {
		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);

		assertThrows(AccountNotFoundException.class, () -> bankService.withdraw(accountNumber, 20d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Withdraw negative or zero amount from a valid account number")
	void testWithdraw_NegativeAmount_ShouldThrowException(Double amount)
			throws AccountNotFoundException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validAccount = new Account();
		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);
		validAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);
		assertThrows(InvalidAmountException.class, () -> bankService.withdraw(100L, amount));
	}

	@Test
	@DisplayName("Withdraw amount from valid account but less balance.")
	void testWithdraw_ValidAmountWithLessBalance_ShouldThrowException() throws AccountNotFoundException,
			InvalidAccountException, InvalidAmountException, AccountBalanceException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validAccount = new Account();
		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);

		assertThrows(AccountBalanceException.class, () -> bankService.withdraw(101L, 20d));
	}

	@Test
	@DisplayName("Withdraw amount from valid account")
	void testWithdraw_ValidAmount_ShouldReturnValue() throws AccountNotFoundException, InvalidAccountException,
			InvalidAmountException, AccountBalanceException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validAccount = new Account();
		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);
		validAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);

		doAnswer(invocation -> {
			Account account = invocation.getArgument(0);
			Double newBalance = account.getAccountBalance() - (Double) invocation.getArgument(2);
			account.setAccountBalance(newBalance);
			return 12345L;
		}).when(mockBankDAO).performTransaction(validAccount, TransactionType.DEBIT, 50.0);

		assertEquals(50.0, bankService.withdraw(101L, 50d));
	}

	@ParameterizedTest
	@DisplayName("Transfer from invalid account number")
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testFundTransfer_InvalidFromAccountNo_ShouldThrowException(Long accountNumber) throws AccountNotFoundException,
			InvalidAmountException, InvalidAccountException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validToAccount = new Account();
		validToAccount.setAccountNo(101L);
		validToAccount.setCustomer(customer);
		validToAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);
		when(mockBankDAO.getAccountById(101L)).thenReturn(validToAccount);

		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer(accountNumber, 101L, 20d));
	}

	@ParameterizedTest
	@DisplayName("Transfer to invalid account number")
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testFundTransfer_InvalidToAccountNo_ShouldThrowException(Long accountNumber)
			throws AccountNotFoundException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");
		Account validFromAccount = new Account();
		validFromAccount.setAccountNo(101L);
		validFromAccount.setCustomer(customer);
		validFromAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validFromAccount);
		when(mockBankDAO.getAccountById(accountNumber)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer(101L, accountNumber, 20d));
	}

	@Test
	@DisplayName("Transfer to same account number")
	void testFundTransfer_SameAccount_ShouldThrowException() {
		assertThrows(InvalidFundTransferException.class, () -> bankService.fundTransfer(101L, 101L, 100d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Transfer negative or zero balance should give apt error.")
	void testFundTransfer_NegativeAmount_ShouldThrowException(Double amount) {
		assertThrows(InvalidAmountException.class, () -> bankService.fundTransfer(101L, 102L, amount));
	}

	@Test
	@DisplayName("Transfer from valid amount with low balance")
	void testFundTransfer_FromValidAccountNoWithLowBalance_ShouldThrowException()
			throws AccountNotFoundException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");

		Account validFromAccount = new Account();
		validFromAccount.setAccountNo(101L);
		validFromAccount.setCustomer(customer);
		validFromAccount.setAccountBalance(0.0);

		Account validToAccount = new Account();
		validToAccount.setAccountNo(102L);
		validToAccount.setCustomer(customer);
		validToAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validFromAccount);
		when(mockBankDAO.getAccountById(102L)).thenReturn(validToAccount);

		assertThrows(AccountBalanceException.class, () -> bankService.fundTransfer(101L, 102L, 100.0));
	}

	@Test
	@DisplayName("Valid Fund Transfer Transaction")
	void testFundTransfer_ValidCase_ShouldReturnTrue() throws InvalidFundTransferException, AccountNotFoundException,
			AccountBalanceException, InvalidAmountException, InvalidAccountException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");

		Account validFromAccount = new Account();
		validFromAccount.setAccountNo(101L);
		validFromAccount.setCustomer(customer);
		validFromAccount.setAccountBalance(100.0);

		Account validToAccount = new Account();
		validToAccount.setAccountNo(102L);
		validToAccount.setCustomer(customer);
		validToAccount.setAccountBalance(100.0);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validFromAccount);
		when(mockBankDAO.getAccountById(102L)).thenReturn(validToAccount);

		doAnswer(invocation -> {
			Account fromAccount = invocation.getArgument(0);
			Account toAccount = invocation.getArgument(1);
			Double txnAmount = invocation.getArgument(2);

			fromAccount.setAccountBalance(fromAccount.getAccountBalance() - txnAmount);
			toAccount.setAccountBalance(toAccount.getAccountBalance() + txnAmount);

			return 12345L;
		}).when(mockBankDAO).performTransaction(validFromAccount, validToAccount, 50.0);

		assertTrue(bankService.fundTransfer(101L, 102L, 50.0));
	}

	@ParameterizedTest
	@DisplayName("Get All Transactions for invalid account number")
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testGetAllTransactions_InvalidAccountNo_ShouldThrowException(Long accountNo)
			throws AccountNotFoundException, TransactionFailedException {
		when(mockBankDAO.getAccountById(accountNo)).thenThrow(AccountNotFoundException.class);

		assertThrows(AccountNotFoundException.class, () -> bankService.getAllTransactionDetails(accountNo));
	}

	@Test
	@DisplayName("Get All Transactions for valid account number should return 10 transactions")
	void testGetAllTransactions_ValidAccountNo_ShouldReturnList()
			throws AccountNotFoundException, NoTransactionsFoundException, TransactionFailedException {
		Customer customer = new Customer();
		Account validAccount = new Account();
		List<Transaction> dummyTransactions = new ArrayList<>();

		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");

		for (int i = 0; i < 20; i++) {
			Transaction transaction = new Transaction();
			transaction.setTransactionId((long) i);
			transaction.setTransactionType(TransactionType.CREDIT);
			transaction.setToAccount(validAccount);
			transaction.setTransactionAmount(10d);
			dummyTransactions.add(transaction);
		}

		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);
		validAccount.setAccountBalance(100.0);
		validAccount.setTransactions(dummyTransactions);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);

		assertInstanceOf(List.class, bankService.getAllTransactionDetails(101L));
	}

	@Test
	@DisplayName("Get All Transactions for valid account number should throw No Transactions Found")
	void testGetAllTransactions_ValidAccountNo_ShouldThrowException()
			throws AccountNotFoundException, NoTransactionsFoundException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Valid Customer");

		List<Transaction> dummyTransactions = new ArrayList<>();

		Account validAccount = new Account();
		validAccount.setAccountNo(101L);
		validAccount.setCustomer(customer);
		validAccount.setAccountBalance(100.0);
		validAccount.setTransactions(dummyTransactions);

		when(mockBankDAO.getAccountById(101L)).thenReturn(validAccount);

		assertThrows(NoTransactionsFoundException.class, () -> bankService.getAllTransactionDetails(101L));
	}
}
