package com.cg.bankapp.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.exceptions.TransactionUpdationException;
import com.cg.bankapp.util.BankDatabase;

class BankServiceTest {
	IBankService bankService = new BankServiceImpl();
	Account testAccount101 = new Account("test101", new Customer());
	Account testAccount102 = new Account("test101", new Customer());

	@BeforeEach
	void setup() {
		BankDatabase.getBankDatabase().put("test101", testAccount101);
		BankDatabase.getBankDatabase().put("test102", testAccount102);
	}

	@AfterEach
	void cleanup() {
		BankDatabase.getBankDatabase().remove("test101");
		BankDatabase.getBankDatabase().remove("test102");
	}

	@ParameterizedTest
	@DisplayName("Show balance for invalid account number")
	@ValueSource(strings = { "123456789" })
	@NullSource
	void testShowBalance_InvalidAccountNo_ShouldThrowException(String accountNumber) {
		assertThrows(AccountNotFoundException.class, () -> bankService.showBalance(accountNumber));
	}

	@Test
	@DisplayName("Show balance for valid account number should return value")
	void testShowBalance_ValidAccountNo_ShouldReturnValue() throws AccountNotFoundException {
		assertEquals(0d, bankService.showBalance("test101"));
	}

	@ParameterizedTest
	@DisplayName("Deposit amount in invalid account number")
	@ValueSource(strings = { "123456789" })
	@NullSource
	void testDeposit_InvalidAccountNo_ShouldThrowException(String accountNumber) {
		assertThrows(AccountNotFoundException.class, () -> bankService.deposit(accountNumber, 20d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Deposit negative or zero amount in a valid account number")
	void testDeposit_NegativeAmount_ShouldThrowException(Double amount) {
		assertThrows(InvalidAmountException.class, () -> bankService.deposit("test101", amount));
	}

	@Test
	@DisplayName("Deposit valid amount in valid account number")
	void testDeposit_ValidAccountNo_ShouldReturnValue() {
		assertAll(() -> assertEquals(20d, bankService.deposit("test101", 20d)),
				() -> assertEquals(1, testAccount101.getTransactions().size()));
	}

	@ParameterizedTest
	@DisplayName("Withdraw amount from invalid account number")
	@ValueSource(strings = { "123456789" })
	@NullSource
	void testWithdraw_InvalidAccountNo_ShouldThrowException(String accountNumber)
			throws AccountNotFoundException, InvalidAmountException, AccountBalanceException {
		assertThrows(AccountNotFoundException.class, () -> bankService.withdraw(accountNumber, 20d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Withdraw negative or zero amount from valid account number")
	void testWithdraw_NegativeAmount_ShouldThrowException(Double amount) {
		assertThrows(InvalidAmountException.class, () -> bankService.withdraw("test101", amount));
	}

	@Test
	@DisplayName("Withdraw valid amount from valid account number with low balance")
	void testWithdraw_ValidAccountNoWithLowBalance_ShouldThrowException() {
		assertThrows(AccountBalanceException.class, () -> bankService.withdraw("test101", 100d));
	}

	@Test
	@DisplayName("Withdraw valid amount from valid account number")
	void testWithdraw_ValidAccountNo_ShouldReturnValue()
			throws AccountNotFoundException, InvalidAmountException, AccountBalanceException {
		testAccount101.setAccountBalance(100d);
		assertAll(() -> assertEquals(80d, bankService.withdraw("test101", 20d)),
				() -> assertEquals(1, testAccount101.getTransactions().size()));
	}

	@ParameterizedTest
	@DisplayName("Transfer from invalid account number")
	@ValueSource(strings = { "123456789" })
	@NullSource
	void testTransfer_InvalidFromAccountNo_ShouldThrowException(String accountNumber) {
		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer(accountNumber, "test101", 20d));
	}

	@ParameterizedTest
	@DisplayName("Transfer to invalid account number")
	@ValueSource(strings = { "123456789" })
	@NullSource
	void testTransfer_InvalidToAccountNo_ShouldThrowException(String accountNumber) {
		assertThrows(AccountNotFoundException.class, () -> bankService.fundTransfer("test101", accountNumber, 20d));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -20d, 0d })
	@NullSource
	@DisplayName("Transfer negative or zero balance should give apt error.")
	void testTransfer_NegativeAmount_ShouldThrowException(Double amount) {
		assertThrows(InvalidAmountException.class, () -> bankService.fundTransfer("test101", "test102", amount));
	}

	@Test
	@DisplayName("Transfer to same account number")
	void testTransfer_SameAccount_ShouldThrowException() {
		assertThrows(InvalidFundTransferException.class, () -> bankService.fundTransfer("test101", "test101", 100d));
	}

	@Test
	@DisplayName("Transfer from valid amount with low balance")
	void testTransfer_FromValidAccountNoWithLowBalance_ShouldThrowException() {
		assertThrows(AccountBalanceException.class, () -> bankService.fundTransfer("test101", "test102", 100d));
	}

	@Test
	@DisplayName("Valid Transaction")
	void testTransfer_ValidCase_ShouldReturnTrue() {
		testAccount101.setAccountBalance(100d);
		assertAll(() -> assertTrue(bankService.fundTransfer("test101", "test102", 20d)),
				() -> assertEquals(1, testAccount101.getTransactions().size()),
				() -> assertEquals(1, testAccount102.getTransactions().size()),
				() -> assertSame(testAccount101.getTransactions().get(0), testAccount102.getTransactions().get(0)));
	}

	@ParameterizedTest
	@DisplayName("Transaction details for invalid account number")
	@ValueSource(strings = { "123456789" })
	@NullSource
	void testGetAllTransactionDetails_InvalidAccountNo(String accountNumber) {
		assertThrows(AccountNotFoundException.class, () -> bankService.getAllTransactionDetails(accountNumber));
	}

	@Test
	@DisplayName("Transaction details for valid acccount but no transactions")
	void testGetAllTransactionDetails_ValidAccountZeroTxn_ShouldThrowException() {
		assertThrows(NoTransactionsFoundException.class, () -> bankService.getAllTransactionDetails("test101"));
	}

	@ParameterizedTest
	@DisplayName("Last 10 Transactions only")
	@ValueSource(ints = { 1, 10, 20 })
	void testGetAllTransactionDetails_ValidAccount_ShouldReturnArrayListOfSize10(int notxn)
			throws AccountNotFoundException, NoTransactionsFoundException, InvalidAmountException,
			TransactionUpdationException {
		System.out.println("Creating " + notxn + " dummy transaction(s)");
		for (int i = 0; i < notxn; i++) {
			bankService.deposit("test101", 20d);
		}
		if (notxn >= 10) {
			assertEquals(10, bankService.getAllTransactionDetails("test101").size());
		} else {
			assertEquals(notxn, bankService.getAllTransactionDetails("test101").size());
		}
	}

}
