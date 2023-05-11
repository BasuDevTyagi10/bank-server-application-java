package com.cg.bankapp.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;
import com.cg.bankapp.beans.ETransactionType;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.DatabaseConnectionException;
import com.cg.bankapp.exceptions.DatabaseQueryException;
import com.cg.bankapp.exceptions.InvalidAccountException;

class BankDAOTest {
	Connection mockConnection = mock(Connection.class);
	PreparedStatement mockStatement = mock(PreparedStatement.class);
	ResultSet mockResultSet = mock(ResultSet.class);

	@Test
	@DisplayName("BankDAO Constructor Database Connection is set to null")
	void testBankDAOConstructor_DatabaseConnectionNull_ShouldThrowException() {
		Exception exception = assertThrows(DatabaseConnectionException.class, () -> new BankDAOImpl(null));
		assertEquals("Something went wrong while connecting to the database.", exception.getMessage());
	}

	@Test
	@DisplayName("BankDAO Constructor Database Connection is set to valid Connection object")
	void testBankDAOConstructor_DatabaseConnectionNull_ShouldReturnTrue()
			throws DatabaseConnectionException, SQLException {
		BankDAOImpl bankDAO = new BankDAOImpl(mockConnection);
		when(mockConnection.isValid(Mockito.anyInt())).thenReturn(true);
		assertTrue(bankDAO.isDatabaseConnectionValid(0));
	}

	@Test
	@DisplayName("BankDAO Database Connection is invalid")
	void testBankDAOConstructor_DatabaseConnnectionValid_ShouldReturnFalse()
			throws DatabaseConnectionException, SQLException {
		BankDAOImpl bankDAO = new BankDAOImpl(mockConnection);
		when(mockConnection.isValid(Mockito.anyInt())).thenReturn(false);
		assertFalse(bankDAO.isDatabaseConnectionValid(0));
	}

	@Test
	@DisplayName("BankDAO Database Connection is valid")
	void testBankDAOConstructor_DatabaseConnnectionValid_ShouldReturnTrue()
			throws DatabaseConnectionException, SQLException {
		BankDAOImpl bankDAO = new BankDAOImpl(mockConnection);
		when(mockConnection.isValid(Mockito.anyInt())).thenReturn(true);
		assertTrue(bankDAO.isDatabaseConnectionValid(0));
	}

	@Test
	@DisplayName("Save null Account")
	void testSave_NullAccount_ShouldThrowException() throws DatabaseConnectionException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testAccount = null;
		Exception exception = assertThrows(InvalidAccountException.class, () -> bankDAO.save(testAccount));
		assertEquals("Cannot save a null Account object.", exception.getMessage());
	}

	@Test
	@DisplayName("Save Account without Customer")
	void testSave_AccountWithNullCustomer_ShouldThrowException() throws DatabaseConnectionException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testAccount = new Account(null);
		Exception exception = assertThrows(InvalidAccountException.class, () -> bankDAO.save(testAccount));
		assertEquals("Cannot save an Account object without a Customer.", exception.getMessage());
	}

	@Test
	@DisplayName("Save a valid Account")
	void testSave_ValidAccount_ShouldReturnGeneratedId()
			throws SQLException, DatabaseConnectionException, DatabaseQueryException, InvalidAccountException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(new Customer(100, "Test Customer"));
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenReturn(mockStatement);
		doNothing().when(mockStatement).setString(1, "SAVINGS");
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setString(1, "SAVINGS");
		mockStatement.setInt(1, 100);
		when(mockStatement.executeUpdate()).thenReturn(1);
		when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(true);
		when(mockResultSet.getInt("GENERATED_KEY")).thenReturn(100);

		Integer generatedAccountNo = bankDAO.save(testValidAccount);
		assertEquals(100, generatedAccountNo);
	}

	@Test
	@DisplayName("Save a valid Account but query fails")
	void testSave_ValidAccountQueryFails_ShouldThrowException() throws DatabaseConnectionException, SQLException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(new Customer(100, "Test Customer"));
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenReturn(mockStatement);
		when(mockStatement.executeUpdate()).thenReturn(0);

		Exception exception = assertThrows(DatabaseQueryException.class, () -> bankDAO.save(testValidAccount));
		assertEquals("Account not saved properly in the database.", exception.getMessage());
	}

	@Test
	@DisplayName("Save a valid Account but fails and account no. not generated")
	void testSave_ValidAccountQueryFailsAccNoNotGen_ShouldThrowException()
			throws SQLException, DatabaseConnectionException, DatabaseQueryException, InvalidAccountException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(new Customer(100, "Test Customer"));
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenReturn(mockStatement);
		when(mockStatement.executeUpdate()).thenReturn(1);
		when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(false);

		Exception exception = assertThrows(DatabaseQueryException.class, () -> bankDAO.save(testValidAccount));
		assertEquals("Account not saved properly. Account number not generated.", exception.getMessage());
	}

	@Test
	@DisplayName("Save a valid Account but query raises SQLException")
	void testSave_ValidAccountSQLQueryFails_ShouldThrowException() throws SQLException, DatabaseConnectionException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(new Customer(100, "Test Customer"));
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenThrow(SQLException.class);

		Exception exception = assertThrows(DatabaseQueryException.class, () -> bankDAO.save(testValidAccount));
		assertTrue(exception.getMessage().startsWith("Unable to save the account due to Database error:"));
	}

	@ParameterizedTest
	@DisplayName("Get Account with invalid account number")
	@ValueSource(ints = { 0, -1 })
	@NullSource
	void testGetAccountById_InvalidAccountNumber_ShouldThrowException(Integer accountNumber)
			throws AccountNotFoundException, DatabaseConnectionException, SQLException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(false);

		Exception exception = assertThrows(AccountNotFoundException.class,
				(() -> bankDAO.getAccountById(accountNumber)));
		assertEquals("Invalid account number.", exception.getMessage());
	}

	@Test
	@DisplayName("Get Account with valid account number")
	void testGetAccountById_ValidAccountNumber_ShouldBeSame()
			throws DatabaseConnectionException, SQLException, AccountNotFoundException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(100, new Customer(100, "Test Customer"));
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(ResultSet.TYPE_SCROLL_INSENSITIVE),
				Mockito.eq(ResultSet.CONCUR_READ_ONLY))).thenReturn(mockStatement);
		doNothing().when(mockStatement).setInt(1, 100);
		mockStatement.setInt(1, 100);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(true);
		when(mockResultSet.getInt("accNo")).thenReturn(100);
		when(mockResultSet.getDouble("accBalance")).thenReturn(0D);
		when(mockResultSet.getString("accType")).thenReturn("SAVINGS");
		when(mockResultSet.getInt("custId")).thenReturn(100);
		when(mockResultSet.getString("custName")).thenReturn("Test Customer");

		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		doNothing().when(mockStatement).setInt(1, 100);
		mockStatement.setInt(1, 100);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(true, false);
		when(mockResultSet.getInt("txnId")).thenReturn(1);
		when(mockResultSet.getString("txnType")).thenReturn("CREDIT");
		when(mockResultSet.getTimestamp("txnDatetime")).thenReturn(Timestamp.from(Instant.now()));
		when(mockResultSet.getObject("fromAccount")).thenReturn(null);
		when(mockResultSet.getObject("toAccount")).thenReturn(100);
		when(mockResultSet.getDouble("txnAmount")).thenReturn(20d);

		Account actualAccount = bankDAO.getAccountById(100);

		assertEquals(testValidAccount, actualAccount);
	}

	@Test
	@DisplayName("Get Account with valid account number but no transactions")
	void testGetAccountById_ValidAccountNumberNoTransactions_ShouldBeSame()
			throws DatabaseConnectionException, SQLException, AccountNotFoundException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(100, new Customer(100, "Test Customer"));
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(ResultSet.TYPE_SCROLL_INSENSITIVE),
				Mockito.eq(ResultSet.CONCUR_READ_ONLY))).thenReturn(mockStatement);
		doNothing().when(mockStatement).setInt(1, 100);
		mockStatement.setInt(1, 100);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(true);
		when(mockResultSet.getInt("accNo")).thenReturn(100);
		when(mockResultSet.getDouble("accBalance")).thenReturn(0D);
		when(mockResultSet.getString("accType")).thenReturn("SAVINGS");
		when(mockResultSet.getInt("custId")).thenReturn(100);
		when(mockResultSet.getString("custName")).thenReturn("Test Customer");

		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		doNothing().when(mockStatement).setInt(1, 100);
		mockStatement.setInt(1, 100);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(false);

		Account actualAccount = bankDAO.getAccountById(100);

		assertEquals(testValidAccount, actualAccount);
	}

	@Test
	@DisplayName("Get Account with non-existing account number")
	void testGetAccountById_NonExistingAccountNumber_ShouldThrowException()
			throws DatabaseConnectionException, SQLException, AccountNotFoundException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(ResultSet.TYPE_SCROLL_INSENSITIVE),
				Mockito.eq(ResultSet.CONCUR_READ_ONLY))).thenReturn(mockStatement);
		doNothing().when(mockStatement).setInt(1, 100);
		mockStatement.setInt(1, 100);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(false);

		assertThrows(AccountNotFoundException.class, () -> bankDAO.getAccountById(100));
	}

	@Test
	@DisplayName("Get Account with valid account number but query fails")
	void testGetAccountById_ValidAccountNumberQueryFails_ShouldThrowException()
			throws DatabaseConnectionException, SQLException, AccountNotFoundException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(ResultSet.TYPE_SCROLL_INSENSITIVE),
				Mockito.eq(ResultSet.CONCUR_READ_ONLY))).thenReturn(mockStatement);
		doNothing().when(mockStatement).setInt(1, 100);
		mockStatement.setInt(1, 100);
		when(mockStatement.executeQuery()).thenThrow(SQLException.class);

		assertThrows(DatabaseQueryException.class, () -> bankDAO.getAccountById(100));
	}

	@Test
	@DisplayName("Get Account with valid account number but transaction query fails")
	void testGetAccountById_ValidAccountNumberTransactionsFail_ShouldBeSame()
			throws DatabaseConnectionException, SQLException, AccountNotFoundException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(100, new Customer(100, "Test Customer"));
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(ResultSet.TYPE_SCROLL_INSENSITIVE),
				Mockito.eq(ResultSet.CONCUR_READ_ONLY))).thenReturn(mockStatement);
		doNothing().when(mockStatement).setInt(1, 100);
		mockStatement.setInt(1, 100);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(true);
		when(mockResultSet.getInt("accNo")).thenReturn(100);
		when(mockResultSet.getDouble("accBalance")).thenReturn(0D);
		when(mockResultSet.getString("accType")).thenReturn("SAVINGS");
		when(mockResultSet.getInt("custId")).thenReturn(100);
		when(mockResultSet.getString("custName")).thenReturn("Test Customer");

		PreparedStatement transactionMockStatement = mock(PreparedStatement.class);
		when(mockConnection.prepareStatement("SELECT * FROM `Transaction` WHERE ? IN (fromAccount, toAccount);"))
				.thenReturn(transactionMockStatement);
		doNothing().when(transactionMockStatement).setInt(1, 100);
		transactionMockStatement.setInt(1, 100);
		when(transactionMockStatement.executeQuery()).thenThrow(SQLException.class);

		Account actualAccount = bankDAO.getAccountById(100);

		assertEquals(testValidAccount, actualAccount);
	}

	@Test
	@DisplayName("Update Balance of null Account")
	void testUpdateBalance_NullAccount_ShouldThrowException() throws DatabaseConnectionException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		assertThrows(InvalidAccountException.class, () -> bankDAO.updateBalance(null));
	}

	@Test
	@DisplayName("Update Balance of valid Account but query fails")
	void testUpdateBalance_ValidAccount_ShouldThrowException() throws SQLException, DatabaseConnectionException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(100, new Customer(100, "Test Customer"));
		testValidAccount.setAccountBalance(10d);
		Account testValidAccountAfterUpdate = new Account(100, new Customer(100, "Test Customer"));
		testValidAccountAfterUpdate.setAccountBalance(20d);

		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		doNothing().when(mockStatement).setDouble(1, 10d);
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setDouble(1, 10d);
		mockStatement.setInt(2, 100);
		when(mockStatement.executeUpdate()).thenThrow(SQLException.class);

		assertThrows(DatabaseQueryException.class, () -> bankDAO.updateBalance(testValidAccount));

	}

	@Test
	@DisplayName("Update Balance of valid Account but Fetch Balance Fails")
	void testUpdateBalance_ValidAccountFetchBalanceFails_ShouldThrowException()
			throws DatabaseConnectionException, SQLException, DatabaseQueryException, InvalidAccountException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(100, new Customer(100, "Test Customer"));
		testValidAccount.setAccountBalance(10d);
		Account testValidAccountAfterUpdate = new Account(100, new Customer(100, "Test Customer"));
		testValidAccountAfterUpdate.setAccountBalance(20d);

		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		doNothing().when(mockStatement).setDouble(1, 10d);
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setDouble(1, 10d);
		mockStatement.setInt(2, 100);
		when(mockStatement.executeUpdate()).thenReturn(1);

		PreparedStatement mockGetBalanceStatement = mock(PreparedStatement.class);
		when(mockConnection.prepareStatement("SELECT `accBalance` FROM `Account` WHERE `accNo` = ?;"))
				.thenReturn(mockGetBalanceStatement);
		when(mockGetBalanceStatement.executeQuery()).thenThrow(SQLException.class);

		assertThrows(DatabaseQueryException.class, () -> bankDAO.updateBalance(testValidAccount));

	}

	@Test
	@DisplayName("Update Balance of valid Account")
	void testUpdateBalance_ValidAccount_ShouldReturnValue()
			throws DatabaseConnectionException, SQLException, DatabaseQueryException, InvalidAccountException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Account testValidAccount = new Account(100, new Customer(100, "Test Customer"));
		testValidAccount.setAccountBalance(10d);
		Account testValidAccountAfterUpdate = new Account(100, new Customer(100, "Test Customer"));
		testValidAccountAfterUpdate.setAccountBalance(20d);

		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		doNothing().when(mockStatement).setDouble(1, 10d);
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setDouble(1, 10d);
		mockStatement.setInt(2, 100);
		when(mockStatement.executeUpdate()).thenReturn(1);

		PreparedStatement mockGetBalanceStatement = mock(PreparedStatement.class);
		when(mockConnection.prepareStatement("SELECT `accBalance` FROM `Account` WHERE `accNo` = ?;"))
				.thenReturn(mockGetBalanceStatement);
		when(mockGetBalanceStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.getDouble("accBalance")).thenReturn(20d);

		assertEquals(20d, bankDAO.updateBalance(testValidAccount));

	}

	@Test
	@DisplayName("Create Transaction ID Query Fails")
	void testCreateTransaction_ValidTransactionQueryFails_ShouldThrowException()
			throws DatabaseConnectionException, SQLException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Transaction testTransaction = new Transaction(ETransactionType.CREDIT, null, 100, 20d);
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenReturn(mockStatement);
		doNothing().when(mockStatement).setDouble(1, 10d);
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setString(1, testTransaction.getTransactionType().name());
		mockStatement.setObject(2, testTransaction.getFromAccount(), java.sql.Types.INTEGER);
		mockStatement.setObject(3, testTransaction.getToAccount(), java.sql.Types.INTEGER);
		mockStatement.setDouble(4, testTransaction.getTransactionAmount());

		when(mockStatement.executeUpdate()).thenThrow(SQLException.class);

		assertThrows(DatabaseQueryException.class, () -> bankDAO.createTransaction(testTransaction));
	}

	@Test
	@DisplayName("Create Transaction ID Query Fails to update")
	void testCreateTransaction_ValidTransactionQueryFailsToUpdate_ShouldThrowException()
			throws DatabaseConnectionException, SQLException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Transaction testTransaction = new Transaction(ETransactionType.CREDIT, null, 100, 20d);
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenReturn(mockStatement);
		doNothing().when(mockStatement).setDouble(1, 10d);
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setString(1, testTransaction.getTransactionType().name());
		mockStatement.setObject(2, testTransaction.getFromAccount(), java.sql.Types.INTEGER);
		mockStatement.setObject(3, testTransaction.getToAccount(), java.sql.Types.INTEGER);
		mockStatement.setDouble(4, testTransaction.getTransactionAmount());

		when(mockStatement.executeUpdate()).thenReturn(0);

		assertThrows(DatabaseQueryException.class, () -> bankDAO.createTransaction(testTransaction));
	}

	@Test
	@DisplayName("Create Transaction ID not generated")
	void testCreateTransaction_ValidTransactionButIDNotGenerated_ShouldThrowException()
			throws DatabaseConnectionException, SQLException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		Transaction testTransaction = new Transaction(ETransactionType.CREDIT, null, 100, 20d);
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenReturn(mockStatement);
		doNothing().when(mockStatement).setDouble(1, 10d);
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setString(1, testTransaction.getTransactionType().name());
		mockStatement.setObject(2, testTransaction.getFromAccount(), java.sql.Types.INTEGER);
		mockStatement.setObject(3, testTransaction.getToAccount(), java.sql.Types.INTEGER);
		mockStatement.setDouble(4, testTransaction.getTransactionAmount());

		when(mockStatement.executeUpdate()).thenReturn(1);
		when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(false);

		Exception exception = assertThrows(DatabaseQueryException.class,
				() -> bankDAO.createTransaction(testTransaction));

		assertEquals("Transaction ID not generated.", exception.getMessage());
	}

	static Stream<Transaction> transactionProviderStream() {
		return Stream.of(new Transaction(ETransactionType.CREDIT, null, 100, 20d),
				new Transaction(ETransactionType.DEBIT, 100, null, 20d),
				new Transaction(ETransactionType.TRANSFER, 100, 200, 20d));
	}

	@ParameterizedTest
	@DisplayName("Create Transaction Successfully")
	@MethodSource("transactionProviderStream")
	void testCreateTransaction_ValidTransaction_ShouldReturnId(Transaction testTransaction)
			throws DatabaseConnectionException, SQLException, DatabaseQueryException {
		IBankDAO bankDAO = new BankDAOImpl(mockConnection);
		when(mockConnection.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS)))
				.thenReturn(mockStatement);
		doNothing().when(mockStatement).setDouble(1, 10d);
		doNothing().when(mockStatement).setInt(2, 100);
		mockStatement.setString(1, testTransaction.getTransactionType().name());
		mockStatement.setObject(2, testTransaction.getFromAccount(), java.sql.Types.INTEGER);
		mockStatement.setObject(3, testTransaction.getToAccount(), java.sql.Types.INTEGER);
		mockStatement.setDouble(4, testTransaction.getTransactionAmount());

		when(mockStatement.executeUpdate()).thenReturn(1);
		when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
		when(mockResultSet.first()).thenReturn(true);
		when(mockResultSet.getInt("GENERATED_KEY")).thenReturn(1);

		assertEquals(1, bankDAO.createTransaction(testTransaction));
	}
}
