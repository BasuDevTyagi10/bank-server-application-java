package com.cg.bankapp.util.logging;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom logger class for the BankServerApplication created by wrapping the
 * <code>Logger</code> class in log4j.
 */
public class BankAppLogger {

	private final Logger logger;
	private static final String LOG_FILE_PATH = "logs/app.log";

	static {
		// Reset/Clear the log file every time application loads.
		try (FileWriter writer = new FileWriter(LOG_FILE_PATH)) {
			writer.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor to initialize the logger using the passed class.
	 * 
	 * @param cls The Class whose name should be used as the Logger name. If null it
	 *            will default to the calling class.
	 */
	public BankAppLogger(Class<?> cls) {
		logger = LogManager.getLogger(cls);
	}

	/**
	 * Logs a message object with the TRACE level.
	 * 
	 * @param message the message string to log.
	 */
	public void trace(String message) {
		logger.trace(message);
	}

	/**
	 * Logs a message object with the DEBUG level.
	 * 
	 * @param message the message string to log.
	 */
	public void debug(String message) {
		logger.debug(message);
	}

	/**
	 * Logs a message object with the INFO level.
	 * 
	 * @param message the message string to log.
	 */
	public void info(String message) {
		logger.info(message);
	}

	/**
	 * Logs a message object with the WARN level.
	 * 
	 * @param message the message string to log.
	 */
	public void warn(String message) {
		logger.warn(message);
	}

	/**
	 * Logs a message object with the ERROR level.
	 * 
	 * @param message the message string to log.
	 */
	public void error(String message) {
		logger.error(message);
	}

	/**
	 * Logs a message object with the FATAL level.
	 * 
	 * @param message the message string to log.
	 */
	public void fatal(String message) {
		logger.fatal(message);
	}

}
