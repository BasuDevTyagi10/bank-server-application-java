package com.cg.bankapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Customer;
import com.cg.bankapp.beans.EAccountType;
import com.cg.bankapp.beans.ETransactionType;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.DatabaseConnectionException;
import com.cg.bankapp.exceptions.DatabaseQueryException;
import com.cg.bankapp.exceptions.InvalidAccountException;

/**
 * This class implements <code>IBankDAO</code> to interact with
 * <code>BankDatabase</code> by overriding the methods in the parent interface.
 */
public class BankDAOImpl implements IBankDAO {
	private Connection databaseConnection = null;

	public BankDAOImpl(Connection databaseConnection) throws DatabaseConnectionException {
		if (databaseConnection == null) {
			throw new DatabaseConnectionException();
		} else {
			this.databaseConnection = databaseConnection;
		}
	}

	public Boolean isDatabaseConnectionValid(int timeout) throws SQLException {
		return databaseConnection.isValid(timeout);
	}

	@Override
	public Integer save(Account account)
			throws DatabaseConnectionException, DatabaseQueryException, InvalidAccountException {
		if (account == null) {
			throw new InvalidAccountException("Cannot save a null Account object.");
		}
		if (account.getCustomer() == null) {
			throw new InvalidAccountException("Cannot save an Account object without a Customer.");
		}

		Integer generatedAccId = null;
		String sqlQueryString = null;
		PreparedStatement statement = null;

		try {
			sqlQueryString = "INSERT INTO `Account`(`accType`, `custId`) VALUES (?,?);";
			statement = databaseConnection.prepareStatement(sqlQueryString, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, account.getAccountType().name());
			statement.setInt(2, account.getCustomer().getCustomerId());

			if (statement.executeUpdate() >= 1) {
				ResultSet resultSet = statement.getGeneratedKeys();

				if (resultSet.first()) {
					generatedAccId = resultSet.getInt("GENERATED_KEY");
				} else {
					throw new DatabaseQueryException("Account not saved properly. Account number not generated.");
				}
			} else {
				throw new DatabaseQueryException("Account not saved properly in the database.");
			}

		} catch (SQLException e) {
			throw new DatabaseQueryException("Unable to save the account due to Database error: " + e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		return generatedAccId;
	}

	@Override
	public Double updateBalance(Account account)
			throws DatabaseQueryException, DatabaseConnectionException, InvalidAccountException {
		if (account == null) {
			throw new InvalidAccountException("Cannot update a null Account object.");
		}

		Double newBalance = null;
		String sqlQueryString = null;
		PreparedStatement statement = null;

		try {
			sqlQueryString = "UPDATE `Account` SET `accBalance` = ? WHERE `accNo` = ?;";
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.setDouble(1, account.getAccountBalance());
			statement.setInt(2, account.getAccountNo());

			if (statement.executeUpdate() >= 1) {
				sqlQueryString = "SELECT `accBalance` FROM `Account` WHERE `accNo` = ?;";
				newBalance = getNewBalance(sqlQueryString, statement, account.getAccountNo());
			}
		} catch (SQLException e) {
			throw new DatabaseQueryException("Unable to update the account due to Database error: " + e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		return newBalance;
	}

	@Override
	public Account getAccountById(Integer accountNo)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException {
		if (accountNo == null || accountNo <= 0) {
			throw new AccountNotFoundException("Invalid account number.");
		}
		Account account = null;
		String sqlQueryString = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			sqlQueryString = "SELECT * FROM `Account` INNER JOIN `Customer` ON `Account`.`custId` = `Customer`.`custId` WHERE `Account`.`accNo` = ?;";
			statement = databaseConnection.prepareStatement(sqlQueryString, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			statement.setInt(1, accountNo);

			resultSet = statement.executeQuery();

			if (resultSet.first()) {
				account = new Account(resultSet.getInt("accNo"), resultSet.getDouble("accBalance"),
						EAccountType.valueOf(resultSet.getString("accType")),
						new Customer(resultSet.getInt("custId"), resultSet.getString("custName")));
			} else {
				throw new AccountNotFoundException();
			}
		} catch (SQLException e) {
			throw new DatabaseQueryException(
					"Unable to fetch the account details due to Database error: " + e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		if (account != null) {
			try {
				account.setTransactions(fetchAccountTransactions(accountNo));
			} catch (DatabaseQueryException e) {
				account.setTransactions(null);
			}
		}

		return account;
	}

	@Override
	public Integer createTransaction(Transaction transaction)
			throws DatabaseConnectionException, DatabaseQueryException {
		Integer generatedTxnId = null;
		String sqlQueryString = null;
		PreparedStatement statement = null;

		try {
			sqlQueryString = "INSERT INTO `Transaction`(`txnType`, `fromAccount`, `toAccount`, `txnAmount`) VALUES (?,?,?,?)";
			statement = databaseConnection.prepareStatement(sqlQueryString, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, transaction.getTransactionType().name());
			statement.setObject(2, transaction.getFromAccount(), java.sql.Types.INTEGER);
			statement.setObject(3, transaction.getToAccount(), java.sql.Types.INTEGER);
			statement.setDouble(4, transaction.getTransactionAmount());

			if (statement.executeUpdate() >= 1) {
				ResultSet resultSet = statement.getGeneratedKeys();

				if (resultSet.first()) {
					generatedTxnId = resultSet.getInt("GENERATED_KEY");
				} else {
					throw new DatabaseQueryException("Transaction ID not generated.");
				}
			} else {
				throw new DatabaseQueryException("Transaction not saved properly in the database.");
			}
		} catch (SQLException e) {
			throw new DatabaseQueryException("Unable to save the transaction due to Database error: " + e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		return generatedTxnId;
	}

	/**
	 * Helper to fetch balance after update.
	 * 
	 * @param sqlQueryString
	 * @param statement
	 * @param accountNo
	 * @return updated balance.
	 * @throws DatabaseQueryException
	 * @throws SQLException
	 */
	private Double getNewBalance(String sqlQueryString, PreparedStatement statement, Integer accountNo)
			throws DatabaseQueryException, SQLException {
		try {
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.setInt(1, accountNo);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			return resultSet.getDouble("accBalance");
		} catch (SQLException e) {
			throw new DatabaseQueryException("Unable to update the account due to Database error: " + e.getMessage());
		} finally {
			statement.close();
		}
	}

	/**
	 * Fetch all Transactions of an Account.
	 * 
	 * @param accountNo
	 * @return List of transactions
	 * @throws DatabaseQueryException
	 */
	private List<Transaction> fetchAccountTransactions(Integer accountNo) throws DatabaseQueryException {
		String sqlQueryString = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		List<Transaction> transactions = new ArrayList<>();
		try {
			sqlQueryString = "SELECT * FROM `Transaction` WHERE ? IN (fromAccount, toAccount);";
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.setInt(1, accountNo);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				transactions.add(new Transaction(resultSet.getInt("txnId"),
						ETransactionType.valueOf(resultSet.getString("txnType")), resultSet.getTimestamp("txnDatetime"),
						(Integer) resultSet.getObject("fromAccount"), (Integer) resultSet.getObject("toAccount"),
						resultSet.getDouble("txnAmount")));
			}
		} catch (SQLException e) {
			throw new DatabaseQueryException(
					"Unable to fetch the account details due to Database error: " + e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		return transactions;
	}
}
