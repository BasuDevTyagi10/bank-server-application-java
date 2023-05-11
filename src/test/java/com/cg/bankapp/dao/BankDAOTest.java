package com.cg.bankapp.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Customer;
import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.entity.TransactionType;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.TransactionFailedException;
import com.cg.bankapp.util.BankDatabaseConstants;

class BankDAOTest {
	BankDAO bankDAO;
	EntityManager mockEntityManager;

	@BeforeEach
	void setUp() {
		mockEntityManager = mock(EntityManager.class);
		bankDAO = new BankDAOImpl(mockEntityManager);
	}

	@Test
	@DisplayName("Save null Account")
	void testSave_NullAccount_ShouldThrowException() {
		Account testAccount = null;
		Exception exception = assertThrows(InvalidAccountException.class, () -> bankDAO.save(testAccount));
		assertEquals("Cannot save a null Account object.", exception.getMessage());
	}

	@Test
	@DisplayName("Save Account without Customer")
	void testSave_AccountWithNullCustomer_ShouldThrowException() {
		Account testAccount = new Account();
		testAccount.setCustomer(null);
		Exception exception = assertThrows(InvalidAccountException.class, () -> bankDAO.save(testAccount));
		assertEquals("Cannot save an Account object without a Customer.", exception.getMessage());
	}

	@Test
	@DisplayName("Save a valid Account")
	void testSave_ValidAccount_ShouldReturnGeneratedId() throws InvalidAccountException, TransactionFailedException {
		Customer customer = new Customer();
		Account account = new Account();
		account.setCustomer(customer);
		Long generatedAccountNo = 123456L;

		doAnswer(invocation -> {
			Account savedAccount = invocation.getArgument(0);
			savedAccount.setAccountNo(generatedAccountNo);
			return null;
		}).when(mockEntityManager).persist(any(Account.class));

		Long result = bankDAO.save(account);

		verify(mockEntityManager, times(1)).persist(account);

		assertEquals(generatedAccountNo, result);
	}

	@Test
	@DisplayName("Save a valid Account Fails")
	void testSave_ValidAccount_ShouldThrowException() throws InvalidAccountException, TransactionFailedException {
		Customer customer = new Customer();
		Account account = new Account();
		account.setCustomer(customer);

		doThrow(RuntimeException.class).when(mockEntityManager).persist(any(Account.class));
		assertThrows(TransactionFailedException.class, () -> bankDAO.save(account));
	}

	@ParameterizedTest
	@DisplayName("Get Account with invalid account number")
	@ValueSource(longs = { 0, -1 })
	@NullSource
	void testGetAccountById_InvalidAccountNumber_ShouldThrowException(Long accountNumber)
			throws AccountBalanceException {
		Exception exception = assertThrows(AccountNotFoundException.class,
				(() -> bankDAO.getAccountById(accountNumber)));
		assertEquals("Invalid account number.", exception.getMessage());
	}

	@ParameterizedTest
	@DisplayName("Get Account with valid account number fails")
	@ValueSource(longs = { 1, 2, 3, 4, 5 })
	void testGetAccountById_ValidAccountNumberFails_ShouldThrowException(Long accountNo)
			throws AccountNotFoundException, TransactionFailedException {
		Account testValidAccount = new Account();
		testValidAccount.setAccountNo(accountNo);
		testValidAccount.setAccountBalance(1000.0);
		List<Transaction> transactions = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Transaction transaction = new Transaction();
			transaction.setTransactionId(1L);
			transaction.setTransactionAmount(100.0);
			transaction.setTransactionType(TransactionType.CREDIT);
			transactions.add(transaction);
			testValidAccount.setTransactions(transactions);
		}

		doThrow(RuntimeException.class).when(mockEntityManager).find(Account.class, accountNo);

		assertThrows(TransactionFailedException.class, () -> bankDAO.getAccountById(accountNo));
	}

	@ParameterizedTest
	@DisplayName("Get Account with valid account number")
	@ValueSource(longs = { 1, 2, 3, 4, 5 })
	void testGetAccountById_ValidAccountNumber_ShouldBeSame(Long accountNo)
			throws AccountNotFoundException, TransactionFailedException {
		Account testValidAccount = new Account();
		testValidAccount.setAccountNo(accountNo);
		testValidAccount.setAccountBalance(1000.0);
		List<Transaction> transactions = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Transaction transaction = new Transaction();
			transaction.setTransactionId(1L);
			transaction.setTransactionAmount(100.0);
			transaction.setTransactionType(TransactionType.CREDIT);
			transactions.add(transaction);
			testValidAccount.setTransactions(transactions);
		}

		when(mockEntityManager.find(Account.class, accountNo)).thenReturn(testValidAccount);
		@SuppressWarnings("unchecked")
		TypedQuery<Transaction> query = mock(TypedQuery.class);
		when(mockEntityManager.createNamedQuery(BankDatabaseConstants.FIND_TRANSACTIONS_BY_ACCOUNT_NO_IDENTIFIER,
				Transaction.class)).thenReturn(query);
		when(query.setParameter(anyString(), anyLong())).thenReturn(query);
		when(query.setMaxResults(10)).thenReturn(query);
		when(query.getResultList()).thenReturn(transactions);

		Account result = bankDAO.getAccountById(accountNo);

		assertEquals(testValidAccount, result);
	}

	@ParameterizedTest
	@DisplayName("Get Account with valid account number but no transactions")
	@ValueSource(longs = { 1, 2, 3, 4, 5 })
	void testGetAccountById_ValidAccountNumberNoTransactions_ShouldBeSame(Long accountNo)
			throws AccountNotFoundException, TransactionFailedException {
		Account testValidAccount = new Account();
		testValidAccount.setAccountNo(accountNo);
		testValidAccount.setAccountBalance(1000.0);

		when(mockEntityManager.find(Account.class, accountNo)).thenReturn(testValidAccount);
		@SuppressWarnings("unchecked")
		TypedQuery<Transaction> query = mock(TypedQuery.class);
		when(mockEntityManager.createNamedQuery(BankDatabaseConstants.FIND_TRANSACTIONS_BY_ACCOUNT_NO_IDENTIFIER,
				Transaction.class)).thenReturn(query);
		when(query.setParameter(anyString(), anyLong())).thenReturn(query);
		when(query.setMaxResults(10)).thenReturn(query);
		when(query.getResultList()).thenReturn(null);

		Account result = bankDAO.getAccountById(accountNo);

		assertEquals(testValidAccount, result);
	}

	@ParameterizedTest
	@DisplayName("Get Account with non-existing account number")
	@ValueSource(longs = { 1, 2, 3, 4, 5 })
	void testGetAccountById_NonExistingAccountNumber_ShouldThrowException(Long accountNo)
			throws AccountNotFoundException {
		when(mockEntityManager.find(Account.class, accountNo)).thenReturn(null);

		assertThrows(AccountNotFoundException.class, () -> bankDAO.getAccountById(accountNo));
	}

	@Test
	@DisplayName("Perform Deposit Transaction")
	void testPerformTransaction_DepositAmount_ShouldReturnValue() throws TransactionFailedException {
		Double transactionAmount = 50.0;
		Double originalBalance = 100.0;
		Double newBalance = originalBalance + transactionAmount;

		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Test Customer");
		Account account = new Account();
		account.setAccountBalance(originalBalance);
		account.setCustomer(customer);

		doNothing().when(mockEntityManager).persist(any());

		bankDAO.performTransaction(account, TransactionType.CREDIT, 50.0);
		assertEquals(newBalance, account.getAccountBalance());

		verify(mockEntityManager, times(1)).persist(any(Transaction.class));
		verify(mockEntityManager, times(1)).persist(any(Account.class));
	}

	@Test
	@DisplayName("Perform Withdraw Transaction")
	void testPerformTransaction_WithdrawAmount_ShouldReturnValue() throws TransactionFailedException {
		Double transactionAmount = 50.0;
		Double originalBalance = 100.0;
		Double newBalance = originalBalance - transactionAmount;

		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Test Customer");
		Account account = new Account();
		account.setAccountBalance(originalBalance);
		account.setCustomer(customer);

		doNothing().when(mockEntityManager).persist(any());

		bankDAO.performTransaction(account, TransactionType.DEBIT, 50.0);
		assertEquals(newBalance, account.getAccountBalance());

		verify(mockEntityManager, times(1)).persist(any(Account.class));
		verify(mockEntityManager, times(1)).persist(any(Transaction.class));
	}

	@Test
	@DisplayName("Perform Deposit Transaction Fails - Rollback")
	void testPerformDepositTransaction_Fails_ShouldRollbackAndThrowException() throws TransactionFailedException {
		Account account = new Account();
		account.setAccountBalance(100.0);

		doThrow(new RuntimeException()).when(mockEntityManager).persist(any());

		assertThrows(TransactionFailedException.class,
				() -> bankDAO.performTransaction(account, TransactionType.CREDIT, 50.0));

		assertEquals(100.0, account.getAccountBalance());
	}

	@Test
	@DisplayName("Perform Withdraw Transaction Fails - Rollback")
	void testPerformWithdrawTransaction_Fails_ShouldRollbackAndThrowException() throws TransactionFailedException {
		Account account = new Account();
		account.setAccountBalance(100.0);

		doThrow(new RuntimeException()).when(mockEntityManager).persist(any());

		assertThrows(TransactionFailedException.class,
				() -> bankDAO.performTransaction(account, TransactionType.DEBIT, 50.0));

		assertEquals(100.0, account.getAccountBalance());
	}

	@Test
	@DisplayName("Perform Fund Transfer Transaction")
	void testPerformTransaction_FundTransfer_ShouldReturnValue() throws TransactionFailedException {
		Customer customer1 = new Customer();
		customer1.setCustomerId(1L);
		customer1.setCustomerName("Test Customer 1");
		Customer customer2 = new Customer();
		customer2.setCustomerId(2L);
		customer2.setCustomerName("Test Customer 2");

		Account fromAccount = new Account();
		fromAccount.setCustomer(customer1);
		fromAccount.setAccountBalance(100.0);

		Account toAccount = new Account();
		toAccount.setCustomer(customer2);
		toAccount.setAccountBalance(100.0);

		doNothing().when(mockEntityManager).persist(any(Transaction.class));
		doNothing().when(mockEntityManager).persist(fromAccount);
		doNothing().when(mockEntityManager).persist(toAccount);

		bankDAO.performTransaction(fromAccount, toAccount, 50.0);
		assertEquals(50.0, fromAccount.getAccountBalance());
		assertEquals(150.0, toAccount.getAccountBalance());

		verify(mockEntityManager, times(2)).persist(any(Account.class));
		verify(mockEntityManager, times(1)).persist(any(Transaction.class));
	}

	@Test
	@DisplayName("Perform Fund Transfer Transaction Fails - Rollback")
	void testPerformTransaction_FundTransferFails_ShouldRollbackAndThrowException() throws TransactionFailedException {
		Customer customer1 = new Customer();
		customer1.setCustomerId(1L);
		customer1.setCustomerName("Test Customer 1");
		Customer customer2 = new Customer();
		customer2.setCustomerId(2L);
		customer2.setCustomerName("Test Customer 2");

		Account fromAccount = new Account();
		fromAccount.setCustomer(customer1);
		fromAccount.setAccountBalance(100.0);

		Account toAccount = new Account();
		toAccount.setCustomer(customer2);
		toAccount.setAccountBalance(100.0);

		doThrow(new RuntimeException()).when(mockEntityManager).persist(any(Transaction.class));

		assertThrows(TransactionFailedException.class, () -> bankDAO.performTransaction(fromAccount, toAccount, 50.0));

		assertEquals(100.0, fromAccount.getAccountBalance());
		assertEquals(100.0, toAccount.getAccountBalance());
	}
}
