/**
 * 
 */
package com.cg.bankapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.ETransactionType;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.util.BankDatabase;

class TestBankService {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	private IBankService bankService = new BankServiceImpl();

	private Account testAccount101 = BankDatabase.DATABASE[0];
	private Account testAccount102 = BankDatabase.DATABASE[1];

	@BeforeEach
	void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@AfterEach
	void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	@Test
	@DisplayName("Show balance for invalid account number should return null.")
	void testShowBalance_InvalidAccountNo_ShouldReturnNull() {
		assertNull(bankService.showBalance("103"));
		assertEquals("Data not found.", errContent.toString().trim());
	}

	@Test
	@DisplayName("Show balance for valid account number should return value.")
	void testShowBalance_ValidAccountNo_ShouldReturnValue() {
		assertEquals(bankService.showBalance("101"), 0d);
	}

	@Test
	@DisplayName("Deposit amount in invalid account number should return null with apt error msg.")
	void testDeposit_InvalidAccountNo_ShouldReturnNull() {
		assertNull(bankService.deposit("103", 20d));
		assertEquals("Data not found.", errContent.toString().trim());
	}

	@Test
	@DisplayName("Deposit negative amount in valid account number should give apt error message.")
	void testDeposit_NegativeAmount_ShouldThrowException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> bankService.deposit("101", -20d));
		assertEquals("Deposit amount cannot be negative or zero.", exception.getMessage().trim());
	}

	@Test
	@DisplayName("Deposit valid amount in valid account number should return new value and give apt message.")
	void testDeposit_ValidAccountNo_ShouldReturnValue() {
		assertEquals(20d, bankService.deposit("101", 20d));
		test_ValidAccountTransactionObject(testAccount101, ETransactionType.CREDIT);
		assertEquals("Rs. 20.00 CREDITED to Account No. 101.", outContent.toString().trim());
	}

	@Test
	@DisplayName("Withdraw amount in invalid account number should return null with apt error msg.")
	void testWithdraw_InvalidAccountNo_ShouldReturnNull() {
		assertNull(bankService.withdraw("103", 20d));
		assertEquals("Data not found.", errContent.toString().trim());
	}

	@Test
	@DisplayName("Withdraw negative amount in valid account number should give apt error message.")
	void testWithdraw_NegativeAmount_ShouldThrowException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> bankService.withdraw("101", -20d));
		assertEquals("Withdraw amount cannot be negative or zero.", exception.getMessage().trim());
	}

	@Test
	@DisplayName("Withdraw valid amount in valid account number should return new value and give apt message.")
	void testWithdraw_ValidAccountNo_ShouldReturnValue() {
		testAccount101.setAccountBalance(100d);
		assertEquals(80d, bankService.withdraw("101", 20d));
		test_ValidAccountTransactionObject(testAccount101, ETransactionType.DEBIT);
		assertEquals("Rs. 20.00 DEBITED from Account No. 101.", outContent.toString().trim());
	}

	@Test
	@DisplayName("Withdraw valid amount in valid account number should return new value and give apt message.")
	void testWithdraw_ValidAccountNoLowBalance_ShouldThrowException() {
		assertNull(bankService.withdraw("101", 100d));
		assertEquals("Balance is insufficient.", errContent.toString().trim());
	}

	@Test
	@DisplayName("Transfer using invalid account number.")
	void testTransfer_InvalidFromAccountNo_ShouldReturnNull() {
		assertNull(bankService.fundTransfer("103", "101", 20d));
		assertEquals("Data not found.", errContent.toString().trim());
	}

	@Test
	@DisplayName("Transfer using invalid account number.")
	void testTransfer_InvalidToAccountNo_ShouldReturnNull() {
		assertNull(bankService.fundTransfer("101", "103", 20d));
		assertEquals("Data not found.", errContent.toString().trim());
	}

	@Test
	@DisplayName("Transfer negative balance should give apt error.")
	void testTransfer_NegativeAmount_ShouldThrowException() {
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> bankService.fundTransfer("101", "102", -20d));
		assertEquals("Transfer amount cannot be negative or zero.", exception.getMessage().trim());
	}

	@Test
	@DisplayName("If Transaction Limit on From Account during transfer should return false.")
	void testTransfer_TransactionLimitFromAccount_ShouldReturnFalse() {
		testAccount101.setTransactionArrayIndex(Transaction.getTxnLimit());
		assertFalse(bankService.fundTransfer("101", "102", 10d));
	}

	@Test
	@DisplayName("If Transaction Limit on From Account during transfer should return false.")
	void testTransfer_TransactionLimitToTarget_ShouldReturnFalse() {
		testAccount102.setTransactionArrayIndex(Transaction.getTxnLimit());
		assertFalse(bankService.fundTransfer("101", "102", 10d));
	}

	@Test
	@DisplayName("Correct Transaction")
	void testTransfer_ValidCase_ShouldReturnTrue() {
		testAccount101.setAccountBalance(100d);
		assertTrue(bankService.fundTransfer("101", "102", 20d));
		test_ValidAccountTransactionObject(testAccount101, ETransactionType.TRANSFER);
		test_ValidAccountTransactionObject(testAccount102, ETransactionType.TRANSFER);
		assertSame(testAccount101.getTransactions()[0], testAccount102.getTransactions()[0]);
		assertEquals("Rs. 20.00 TRANSFERRED from Account No. 101 to Account No. 102", outContent.toString().trim());
	}

	@Test
	@DisplayName("Transaction details for invalid accont number")
	void testGetAllTransactionDetails_InvalidAccount() {
		assertNull(bankService.getAllTransactionDetails("103"));
		assertEquals("Data not found.", errContent.toString().trim());
	}

	@Test
	@DisplayName("Transaction details for valid acccount but 0 transactions")
	void testGetAllTransactionDetails_ValidAccountZeroTxn() {
		assertNull(bankService.getAllTransactionDetails("101"));
	}

	@Test
	@DisplayName("Transaction details for valid acccount")
	void testGetAllTransactionDetails_ValidAccount() {
		bankService.deposit("101", 20d);
		assertInstanceOf(Transaction[].class, bankService.getAllTransactionDetails("101"));
	}

	@Test
	@DisplayName("Check Transaction limit.")
	void testCheckTransactionLimit() {
		testAccount101.setTransactionArrayIndex(Transaction.getTxnLimit());
		assertNull(bankService.deposit("101", 20d));
		assertEquals("Account Number: 101 has reached the limit of 10 transactions.", errContent.toString().trim());
	}

	@Test
	@DisplayName("No. of transaction is not negative or null.")
	void test_DatabaseIndexNotNullOrNegative() {
		assertFalse(testAccount101.noOfTransactions() < 0);
		assertNotNull(testAccount101.noOfTransactions());
	}

	void test_ValidAccountTransactionObject(Account account, ETransactionType actualTransactionType) {
		Transaction testTransaction = account.getTransactions()[0];
		assertNotNull(testTransaction);
		assertTrue(testTransaction.getTransactionId().startsWith("TXN-"));
		assertEquals(testTransaction.getTransactionType(), actualTransactionType);
	}

	@AfterEach
	void cleanUp() {
		testAccount101.setAccountBalance(0d);
		testAccount101.getTransactions()[0] = null;
		testAccount101.setTransactionArrayIndex(0);
		testAccount102.setAccountBalance(0d);
		testAccount102.getTransactions()[0] = null;
		testAccount102.setTransactionArrayIndex(0);
	}

}
