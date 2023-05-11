package com.cg.bankapp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class holds the database of our BankServerApplication.
 */

public class BankDatabase {
	private static final String DATABASE_NAME = "BankApp_JDBC";
	private Connection databaseConnection = null;
	private PreparedStatement statement;

	public BankDatabase() {
		this.databaseConnection = new DatabaseConnection().getDatabaseConnection();

		try {
			createDatabase();
		} catch (SQLException e) {
			System.err.println("Unable to start the application as database is not loaded properly.");
			System.err.println("Error: " + e.getMessage());
			System.exit(0);
		}

		this.databaseConnection = new DatabaseConnection().getDatabaseConnection(DATABASE_NAME);
		try {
			createTables();
			createCustomersInDatabase();
			createAccountsInDatabase();
			resetTransactionsInDatabase();
		} catch (SQLException e) {
			System.err.println("Unable to start the application as database is not loaded properly.");
			System.err.println("Error: " + e.getMessage());
			System.exit(0);
		}
	}

	private void createDatabase() throws SQLException {
		String sqlQueryString = String.format("CREATE DATABASE IF NOT EXISTS `%s`;", DATABASE_NAME);
		queryLogger(sqlQueryString);

		statement = databaseConnection.prepareStatement(sqlQueryString);
		try {
			statement.execute();
		} finally {
			statement.close();
		}

	}

	private void createTables() throws SQLException {
		createCustomerTable();
		createAccountTable();
		createTransactionTable();
	}

	private void createCustomerTable() throws SQLException {
		String sqlQueryString = "CREATE TABLE IF NOT EXISTS `Customer`(\r\n"
				+ "\t`custId` INT AUTO_INCREMENT NOT NULL,\n" + "\t`custName` VARCHAR(50) NOT NULL DEFAULT 'USER',\r\n"
				+ "\tPRIMARY KEY(`custId`)\r\n" + "\t);";
		queryLogger(sqlQueryString);

		statement = databaseConnection.prepareStatement(sqlQueryString);
		try {
			statement.execute();
		} finally {
			statement.close();
		}
	}

	private void createAccountTable() throws SQLException {
		String sqlQueryString = "CREATE TABLE IF NOT EXISTS `Account`(\r\n"
				+ "\t`accNo` INT AUTO_INCREMENT NOT NULL,\r\n" + "\t`accType` VARCHAR(10) NOT NULL,\r\n"
				+ "\t`accBalance` FLOAT DEFAULT 0,\r\n" + "\t`custId` INT,\r\n" + "\tPRIMARY KEY(`accNo`),\r\n"
				+ "\tFOREIGN KEY(`custId`) REFERENCES `Customer`(`custId`)\r\n" + "\t);";
		queryLogger(sqlQueryString);

		statement = databaseConnection.prepareStatement(sqlQueryString);
		try {
			statement.execute();
		} finally {
			statement.close();
		}
	}

	private void createTransactionTable() throws SQLException {
		String sqlQueryString = "CREATE TABLE IF NOT EXISTS `Transaction`(\r\n"
				+ "\t`txnId` INT AUTO_INCREMENT NOT NULL,\r\n" + "\t`txnType` VARCHAR(10) NOT NULL,\r\n"
				+ "\t`txnDatetime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\r\n" + "\t`fromAccount` INT,\r\n"
				+ "\t`toAccount` INT,\r\n" + "\t`txnAmount` FLOAT NOT NULL,\r\n" + "\tPRIMARY KEY(`txnId`),\r\n"
				+ "\tFOREIGN KEY(`fromAccount`) REFERENCES `Account`(`accNo`),\r\n"
				+ "\tFOREIGN KEY(`toAccount`) REFERENCES `Account`(`accNo`)\r\n" + "\t);";
		queryLogger(sqlQueryString);

		statement = databaseConnection.prepareStatement(sqlQueryString);
		try {
			statement.execute();
		} finally {
			statement.close();
		}
	}

	private void createCustomersInDatabase() throws SQLException {
		String sqlQueryString;

		try {
			sqlQueryString = DatabaseConnection.UNSET_FK_CONSTRAINT;
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			sqlQueryString = "DELETE FROM `Customer`;";
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			sqlQueryString = "ALTER TABLE `Customer` AUTO_INCREMENT = 1;";
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			String[] customerNames = { "Basudev Tyagi", "Arjun Upadhyay", "Sonam Sauntiyal", "Kartik Singhal",
					"Anjali Dabral", "Tanishka Rana", "Himanshu Negi", "Garvit Chawla", "Vivek Chamoli",
					"Ashish Karki", };
			for (String customerName : customerNames) {
				sqlQueryString = "INSERT INTO `Customer`(`custName`) VALUES (?);";
				statement = databaseConnection.prepareStatement(sqlQueryString);
				statement.setString(1, customerName);
				queryLogger(statement.toString().split(": ")[1]);
				statement.execute();
			}

			sqlQueryString = DatabaseConnection.SET_FK_CONSTRAINT;
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

		} finally {
			statement.close();
		}
	}

	private void createAccountsInDatabase() throws SQLException {
		String sqlQueryString;

		try {
			sqlQueryString = DatabaseConnection.UNSET_FK_CONSTRAINT;
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			sqlQueryString = "DELETE FROM `Account`;";
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			sqlQueryString = "ALTER TABLE `Account` AUTO_INCREMENT = 1;";
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			for (Integer custId = 1; custId <= 10; custId++) {
				sqlQueryString = "INSERT INTO `Account`(`accType`, `custId`) VALUES (?,?);";
				statement = databaseConnection.prepareStatement(sqlQueryString);
				statement.setString(1, "SAVINGS");
				statement.setInt(2, custId);
				queryLogger(statement.toString().split(": ")[1]);
				statement.execute();
			}

			sqlQueryString = DatabaseConnection.SET_FK_CONSTRAINT;
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

		} finally {
			statement.close();
		}
	}

	private void resetTransactionsInDatabase() throws SQLException {
		String sqlQueryString;

		try {
			sqlQueryString = DatabaseConnection.UNSET_FK_CONSTRAINT;
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			sqlQueryString = "DELETE FROM `Transaction`;";
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			sqlQueryString = "ALTER TABLE `Transaction` AUTO_INCREMENT = 1;";
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

			sqlQueryString = DatabaseConnection.SET_FK_CONSTRAINT;
			queryLogger(sqlQueryString);
			statement = databaseConnection.prepareStatement(sqlQueryString);
			statement.execute();

		} finally {
			statement.close();
		}
	}

	private void queryLogger(String query) {
		System.out.println("RUNNING QUERY:\n\t" + query);
	}

	public Connection getDatabaseConnection() {
		return new DatabaseConnection().getDatabaseConnection(DATABASE_NAME);
	}

}
