package com.cg.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.cg.bankapp.dto.Account;
import com.cg.bankapp.dto.Customer;
import com.cg.bankapp.exception.AccountBalanceException;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.BankServerException;
import com.cg.bankapp.exception.InvalidAmountException;
import com.cg.bankapp.exception.InvalidFundTransferException;
import com.cg.bankapp.exception.NoTransactionsFoundException;
import com.cg.bankapp.exception.TransactionFailedException;
import com.cg.bankapp.model.AccountEntity;
import com.cg.bankapp.model.CustomerEntity;
import com.cg.bankapp.model.TransactionEntity;
import com.cg.bankapp.repository.BankRepository;
import com.cg.bankapp.utils.AccountType;
import com.cg.bankapp.utils.TransactionType;

class BankServiceTest {

	BankService bankService;
	BankRepository mockBankRepository;

	CustomerEntity validCustomer;
	AccountEntity validAccount;

	Customer validCustomerDto;
	Account validAccountDto;

	AccountEntity validFromAccount;
	AccountEntity validToAccount;

	@BeforeEach
	void setUp() {
		mockBankRepository = mock(BankRepository.class);
		bankService = new BankServiceImpl(mockBankRepository);

		validCustomer = new CustomerEntity();
		validCustomer.setCustomerId(1L);
		validCustomer.setCustomerName("Test Customer");

		validAccount = new AccountEntity();
		validAccount.setAccountNo(1L);
		validAccount.setAccountType(AccountType.SAVINGS);
		validAccount.setCustomer(validCustomer);
		validAccount.setAccountBalance(0.0);

		validCustomerDto = new Customer();
		validCustomerDto.setCustomerId(1L);
		validCustomerDto.setCustomerName("Test Customer");

		validAccountDto = new Account();
		validAccountDto.setAccountNo(1L);
		validAccountDto.setAccountType(AccountType.SAVINGS);
		validAccountDto.setCustomer(validCustomerDto);
		validAccountDto.setAccountBalance(0.0);

		validFromAccount = new AccountEntity();
		validFromAccount.setAccountNo(101L);
		validFromAccount.setCustomer(validCustomer);
		validFromAccount.setAccountBalance(0.0);

		validToAccount = new AccountEntity();
		validToAccount.setAccountNo(102L);
		validToAccount.setCustomer(validCustomer);
		validToAccount.setAccountBalance(0.0);

	}

	@Test
	void testCreateAccount_ValidCustomer_ShouldReturnAccount() throws BankServerException {
		when(mockBankRepository.save(any(AccountEntity.class))).thenReturn(validAccount);
		assertEquals(validAccountDto, bankService.createAccount(validCustomerDto));
	}

	@Test
	void testCreateAccount_SaveAccountFails_ShouldThrowException() {
		when(mockBankRepository.save(any(AccountEntity.class))).thenThrow(RuntimeException.class);
		assertThrows(TransactionFailedException.class, () -> bankService.createAccount(validCustomerDto));
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testShowBalance_InvalidAccountNo_ShouldThrowException(Long id)
			throws BankServerException {
		when(mockBankRepository.findById(id)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.showBalance(id));
	}

	@Test
	void testShowBalance_ValidAccountNo_ShouldReturnValue() throws BankServerException {
		validAccount.setAccountBalance(100.0);
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));
		assertEquals(100.0, bankService.showBalance(1L));
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testDeposit_InvalidAccountNo_ShouldThrowException(Long accountNumber)
			throws BankServerException {
		when(mockBankRepository.findById(accountNumber)).thenThrow(AccountNotFoundException.class);

		assertThrows(AccountNotFoundException.class, () -> bankService.deposit(accountNumber, 20d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	void testDeposit_NegativeAmount_ShouldThrowException(Double amount)
			throws AccountNotFoundException, TransactionFailedException {
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));
		assertThrows(InvalidAmountException.class, () -> bankService.deposit(1L, amount));
	}

	@Test
	void testDeposit_ValidAmount_ShouldReturnValue() throws BankServerException {
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));
		when(mockBankRepository.save(validAccount)).thenReturn(validAccount);

		bankService.deposit(1L, 50d);
		assertEquals(50.0, validAccount.getAccountBalance());
	}

	@Test
	void testDeposit_AccountSaveActionFailed() {
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));
		when(mockBankRepository.save(validAccount)).thenThrow(RuntimeException.class);
		
		assertThrows(TransactionFailedException.class, () -> bankService.deposit(1L, 50.0));
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testWithdraw_InvalidAccountNo_ShouldThrowException(Long accountNumber)
			throws BankServerException {
		when(mockBankRepository.findById(accountNumber)).thenThrow(AccountNotFoundException.class);

		assertThrows(AccountNotFoundException.class, () -> bankService.withdraw(accountNumber, 20d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	void testWithdraw_NegativeAmount_ShouldThrowException(Double amount)
			throws BankServerException {
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));
		assertThrows(InvalidAmountException.class, () -> bankService.withdraw(1L, amount));
	}

	@Test
	void testWithdraw_ValidAmountWithLessBalance_ShouldThrowException() throws BankServerException {
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));

		assertThrows(AccountBalanceException.class, () -> bankService.withdraw(1L, 20d));
	}

	@Test
	void testWithdraw_ValidAmount_ShouldReturnValue() throws BankServerException {
		validAccount.setAccountBalance(100.0);
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));
		when(mockBankRepository.save(validAccount)).thenReturn(validAccount);

		bankService.withdraw(1L, 50d);
		assertEquals(50.0, validAccount.getAccountBalance());
	}

	@Test
	void testWithdraw_AccountSaveActionFailed() {
		validAccount.setAccountBalance(100.0);
		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));
		when(mockBankRepository.save(validAccount)).thenThrow(RuntimeException.class);

		assertThrows(TransactionFailedException.class, () -> bankService.withdraw(1L, 50.0));
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testFundTransfer_InvalidFromAccountNo_ShouldThrowException(Long accountNumber) throws BankServerException {
		when(mockBankRepository.findById(accountNumber)).thenThrow(AccountNotFoundException.class);
		when(mockBankRepository.findById(101L)).thenReturn(Optional.ofNullable(validAccount));

		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer(accountNumber, 101L, 20d));
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testFundTransfer_InvalidToAccountNo_ShouldThrowException(Long accountNumber)
			throws AccountNotFoundException, TransactionFailedException {
		when(mockBankRepository.findById(101L)).thenReturn(Optional.ofNullable(validAccount));
		when(mockBankRepository.findById(accountNumber)).thenThrow(AccountNotFoundException.class);
		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer(101L, accountNumber, 20d));
	}

	@Test
	void testFundTransfer_SameAccount_ShouldThrowException() {
		assertThrows(InvalidFundTransferException.class, () -> bankService.fundTransfer(101L, 101L, 100d));
	}

	@Test
	void testFundTransfer_FromValidAccountNoWithLowBalance_ShouldThrowException()
			throws BankServerException {
		when(mockBankRepository.findById(101L)).thenReturn(Optional.ofNullable(validFromAccount));
		when(mockBankRepository.findById(102L)).thenReturn(Optional.ofNullable(validToAccount));

		assertThrows(AccountBalanceException.class, () -> bankService.fundTransfer(101L, 102L, 100.0));
	}

	@Test
	void testFundTransfer_ValidCase_ShouldReturnValue() throws BankServerException {
		validFromAccount.setAccountBalance(100.0);

		when(mockBankRepository.findById(101L)).thenReturn(Optional.ofNullable(validFromAccount));
		when(mockBankRepository.findById(102L)).thenReturn(Optional.ofNullable(validToAccount));

		when(mockBankRepository.save(validFromAccount)).thenReturn(validFromAccount);

		bankService.fundTransfer(101L, 102L, 50.0);
		assertEquals(50.0, validFromAccount.getAccountBalance());
		assertEquals(50.0, validToAccount.getAccountBalance());
	}

	@Test
	void testFundTransfer_SaveActionFails_ShouldThrowException() {
		validFromAccount.setAccountBalance(100.0);

		when(mockBankRepository.findById(101L)).thenReturn(Optional.ofNullable(validFromAccount));
		when(mockBankRepository.findById(102L)).thenReturn(Optional.ofNullable(validToAccount));

		when(mockBankRepository.save(validFromAccount)).thenThrow(RuntimeException.class);

		assertThrows(TransactionFailedException.class, () -> bankService.fundTransfer(101L, 102L, 50.0));

	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	void testFundTransfer_NegativeAmount_ShouldThrowException(Double amount) {
		assertThrows(InvalidAmountException.class, () -> bankService.fundTransfer(101L, 102L, amount));
	}

	@ParameterizedTest
	@ValueSource(longs = { -1, 0 })
	@NullSource
	void testGetAllTransactions_InvalidAccountNo_ShouldThrowException(Long accountNo)
			throws BankServerException {
		when(mockBankRepository.findById(accountNo)).thenThrow(AccountNotFoundException.class);

		assertThrows(AccountNotFoundException.class, () -> bankService.getAllTransactionDetails(accountNo));
	}

	@Test
	void testGetAllTransactions_ValidAccountNoButNoTransactions_ShouldThrowException() throws BankServerException {
		List<TransactionEntity> dummyTransactions = new ArrayList<>();
		validAccount.setTransactions(dummyTransactions);

		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));

		assertThrows(NoTransactionsFoundException.class, () -> bankService.getAllTransactionDetails(1L));
	}

	@Test
	void testGetAllTransactions_ValidAccountNo_ShouldReturn10Txns() throws BankServerException {
		List<TransactionEntity> dummyTransactions = new ArrayList<>();

		for (long i = 0; i < 20; i++) {
			TransactionEntity transaction = new TransactionEntity();
			transaction.setTransactionId(i);
			transaction.setTransactionType(TransactionType.CREDIT);
			transaction.setToAccount(validAccount);
			transaction.setTransactionAmount(10d);
			dummyTransactions.add(transaction);
		}

		validAccount.setTransactions(dummyTransactions);

		when(mockBankRepository.findById(1L)).thenReturn(Optional.ofNullable(validAccount));

		assertEquals(10, bankService.getAllTransactionDetails(1L).size());
	}

}
