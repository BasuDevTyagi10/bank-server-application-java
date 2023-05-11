package com.cg.bankapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.util.ConsoleFileConstants;
import com.cg.bankapp.util.logging.BankAppLogger;

/**
 * This class contains the methods related to displaying results of user
 * interactions on the console.
 * 
 * This manager uses files to print data on the console using a
 * <code>PrintStream</code> for normal result and error result.
 */
public class ConsoleManager {
	private String applicationName;
	private PrintStream errorStream = System.err;
	private PrintStream outputStream = System.out;
	private Consumer<List<String>> printLinesOnConsole = lines -> lines.stream().forEach(outputStream::println);
	private BankAppLogger bankAppLogger = new BankAppLogger(getClass());

	/**
	 * Constructor to create the instance using the applicationName.
	 */
	public ConsoleManager(String applicationName) {
		this.applicationName = applicationName;
		bankAppLogger.debug(getClass().getName() + " initialized for " + this.applicationName);
	}

	/**
	 * Handler to read a file and print the file contents on the console. This
	 * method can also replace tokens in the file using the replacementsMap and
	 * print some default message if the file is not found or some other IOException
	 * occurs during reading the file.
	 * 
	 * @param filepath        Path of the file which will be read to fetch output.
	 * @param replacementsMap Map of <String, Object> containing tokens with their
	 *                        replacements.
	 * @param defaultLines    A Supplier of List of String to print default lines if
	 *                        the file throws any IOException.
	 * @param printStream     The PrintStream for printing the output after reading
	 *                        the file.
	 */
	private void printFileContent(String filepath, Map<String, Object> replacementsMap,
			Supplier<List<String>> defaultLines, PrintStream printStream) {
		File file = new File(filepath);

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			bankAppLogger.info("Reading file using BufferedReader: " + filepath);
			while ((line = bufferedReader.readLine()) != null) {
				bankAppLogger.debug("Checking if there are any replacement tokens.");
				if (replacementsMap != null && !replacementsMap.isEmpty()) {
					bankAppLogger.debug("Replacement tokens exist. Performing replacements.");
					for (Entry<String, Object> entry : replacementsMap.entrySet()) {
						if (line.contains(entry.getKey())) {
							line = line.replace(entry.getKey(), entry.getValue().toString());
						}
					}
					bankAppLogger.debug(replacementsMap.size() + " replacement done.");
				} else {
					bankAppLogger.debug("No replacements tokens.");
				}
				bankAppLogger.info("Printing output to console.");
				printStream.println(line);
			}
		} catch (IOException e) {
			bankAppLogger.error("Error reading the file: " + filepath + ". Error: " + e.getMessage());
			if (defaultLines != null) {
				bankAppLogger.debug("Print default message.");
				printLinesOnConsole.accept(defaultLines.get());
			}
		}
	}

	/**
	 * Print an output message on the console using System.out PrintStream.
	 * 
	 * @param filepath        Path of the file which will be read to fetch output.
	 * @param replacementsMap Map of <String, Object> containing tokens with their
	 *                        replacements.
	 */
	public void printOutputMessage(String filepath, Map<String, Object> replacementsMap) {
		bankAppLogger.info("Printing output message on console using System.out PrintStream");
		printFileContent(filepath, replacementsMap, null, outputStream);
	}

	/**
	 * Print an output message on the console using System.out PrintStream.
	 * 
	 * @param filepath     Path of the file which will be read to fetch output.
	 * @param defaultLines A Supplier of List of String to print default lines if
	 *                     the file throws any IOException.
	 */
	public void printOutputMessage(String filepath, Supplier<List<String>> defaultLines) {
		bankAppLogger.info("Printing output message on console using System.out PrintStream");
		printFileContent(filepath, null, defaultLines, outputStream);
	}

	/**
	 * * Print an output message on the console using System.out PrintStream.
	 * 
	 * @param filepath        Path of the file which will be read to fetch output.
	 * @param replacementsMap Map of <String, Object> containing tokens with their
	 *                        replacements.
	 * @param defaultLines    A Supplier of List of String to print default lines if
	 *                        the file throws any IOException.
	 */
	public void printOutputMessage(String filepath, Map<String, Object> replacementsMap,
			Supplier<List<String>> defaultLines) {
		bankAppLogger.info("Printing output message on console using System.out PrintStream");
		printFileContent(filepath, replacementsMap, defaultLines, outputStream);
	}

	/**
	 * Print an output message on the console using System.err PrintStream.
	 * 
	 * @param filepath     Path of the file which will be read to fetch output.
	 * @param errorMessage Error message what will be printed by replacing the
	 *                     default token "errorMessage".
	 */
	public void printErrorMessage(String filepath, String errorMessage) {
		bankAppLogger.info("Printing error message on console using System.err PrintStream");
		Map<String, Object> replacements = new HashMap<>();
		replacements.put("<errorMessage>", errorMessage);

		printFileContent(filepath, replacements, null, errorStream);
	}

	/**
	 * Print the title message of the application on the console.
	 */
	public void printTitle() {
		bankAppLogger.info("Printing title message on console using System.out PrintStream");
		Supplier<List<String>> defaultLines = () -> Arrays.asList(applicationName, "Welcome User");
		printOutputMessage(ConsoleFileConstants.PRINT_TITLE_FILEPATH, defaultLines);
	}

	/**
	 * Print the menu message of the application on the console.
	 */
	public void printMenu() {
		bankAppLogger.info("Printing menu on console using System.out PrintStream");
		Supplier<List<String>> defaultLines = () -> Arrays.asList("Unable to fetch menu", "Please try again.");
		printOutputMessage(ConsoleFileConstants.PRINT_MENU_FILEPATH, defaultLines);
	}

	/**
	 * Print the input prefix message of the application on the console.
	 */
	public void printInputPrefix() {
		bankAppLogger.info("Printing input prefix on console using System.out PrintStream");
		outputStream.print("> ");
	}

	/**
	 * Print the input prefix message of the application on the console.
	 */
	public void printExit() {
		bankAppLogger.info("Printing exit message on console using System.out PrintStream");
		Supplier<List<String>> defaultLines = () -> Arrays.asList("\nThank you for using " + applicationName + ".");

		Map<String, Object> replacements = new HashMap<>();
		replacements.put("<applicationName>", applicationName);

		printOutputMessage(ConsoleFileConstants.PRINT_EXIT_FILEPATH, replacements, defaultLines);

	}

	/**
	 * Print the transaction details of an account on the console.
	 */
	public void printTransactionDetails(Long accountNo, List<Transaction> transactions) {
		bankAppLogger.info("Printing transaction details for account number [" + accountNo
				+ "] on console using System.out PrintStream");
		Supplier<List<String>> defaultLines = () -> Arrays.asList("LAST 10 TRANSACTIONS FOR ACCOUNT: " + accountNo,
				"DATE\t\tFROM\t\tTO\t\tAMOUNT\t\tTXN ID\t\tTXN TYPE");

		Map<String, Object> replacements = new HashMap<>();
		replacements.put("<accountNo>", accountNo);

		printOutputMessage(ConsoleFileConstants.PRINT_TXN_DETAILS_HEADER_FILEPATH, replacements, defaultLines);

		String spacer = "\t\t";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		transactions.stream()
				.map(transaction -> dateFormat.format(transaction.getTransactionDatetime()) + "\t"
						+ resolveAccount(transaction.getFromAccount(), accountNo) + spacer
						+ resolveAccount(transaction.getToAccount(), accountNo) + spacer
						+ transaction.getTransactionAmount() + spacer + transaction.getTransactionId() + spacer
						+ transaction.getTransactionType())
				.forEach(outputStream::println);

		printOutputMessage(ConsoleFileConstants.PRINT_TXN_DETAILS_FOOTER_FILEPATH,
				() -> Arrays.asList("----- END OF LIST -----"));

	}

	/**
	 * Handler to resolve the fromAccount and toAccount values for printing on
	 * console.
	 * 
	 * @param getValue     Account object containing the account number
	 * @param compareValue Account number to which the getValue will be compared for
	 *                     resolving
	 * @return The resolved value as String to print on console.
	 */
	private String resolveAccount(Account getValue, Long compareValue) {
		bankAppLogger.debug("Resolving " + Optional.ofNullable(getValue).map(Account::getAccountNo).orElse(null)
				+ " against " + compareValue);
		return Optional.ofNullable(getValue)
				.map(value -> value.getAccountNo().equals(compareValue) ? "SELF" : value.getAccountNo().toString())
				.orElse("-");
	}

}
