package com.cg.bankapp.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
	public static final String SET_FK_CONSTRAINT = "SET FOREIGN_KEY_CHECKS=1;";
	public static final String UNSET_FK_CONSTRAINT = "SET FOREIGN_KEY_CHECKS=0;";
	private Properties properties = new Properties();
	private String mySQLJDBCDriver = null;
	private String dbURL = null;
	private String user = null;
	private String password = null;

	private Connection connection = null;

	public DatabaseConnection() {

		try (FileReader fileReader = new FileReader("src/main/java/com/cg/bankapp/util/config.properties")) {
			properties.load(fileReader);
		} catch (IOException e) {
			System.err.println("Unable to load database properties. " + e.getMessage());
		}

		mySQLJDBCDriver = properties.getProperty("MY_SQL_JBDC_DRIVER");
		dbURL = properties.getProperty("DB_URL");
		user = properties.getProperty("USER");
		password = properties.getProperty("PASSWORD");

		try {
			Class.forName(mySQLJDBCDriver);
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

	public Connection getDatabaseConnection() {
		try {
			connection = DriverManager.getConnection(dbURL, user, password);
		} catch (SQLException e) {
			System.err.println("Error while connecting to database: " + e.getMessage());
		}
		return connection;
	}

	public Connection getDatabaseConnection(String database) {
		try {
			connection = DriverManager.getConnection(dbURL + database, user, password);
		} catch (SQLException e) {
			System.err.println("Error while connecting to database: " + e.getMessage());
		}
		return connection;
	}

}
