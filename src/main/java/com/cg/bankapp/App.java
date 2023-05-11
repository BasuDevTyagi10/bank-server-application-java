package com.cg.bankapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.ExceptionHandlerConstants;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.exceptions.TransactionFailedException;
import com.cg.bankapp.service.BankService;
import com.cg.bankapp.util.ConsoleFileConstants;
import com.cg.bankapp.util.logging.BankAppLogger;
import com.cg.exceptionhandler.ExceptionHandler;

/**
 * This class is used to build the application's user interface and handle user
 * interactions.
 */
public class App {
	/**
	 * The application's name.
	 */
	public static final String APPLICATION_NAME = "BANK SERVER APPLICATION";

	ApplicationContext context = new ClassPathXmlApplicationContext("cst-main-config.xml");
	BankService bankService = context.getBean("bankServiceImpl", BankService.class);
	private ExceptionHandler exceptionHandler = context.getBean("exceptionHandler", ExceptionHandler.class);
	private ConsoleManager consoleManager = context.getBean("consoleManager", ConsoleManager.class);

	private Scanner scanner = new Scanner(System.in);
	private BankAppLogger bankAppLogger = new BankAppLogger(getClass());

	/**
	 * Constructor to initialize the application.
	 */
	public App() {
		bankAppLogger.info("Initailized application instance for: " + APPLICATION_NAME);
		List<String> customerNames = Arrays.asList("Basudev Tyagi", "Arjun Upadhyay", "Sonam Sauntiyal",
				"Kanupriya Prajapati", "Anjali Dabral", "Tanishka Rana", "Himanshu Negi", "Garvit Chawla",
				"Vivek Chamoli", "Ashish Karki");
		for (String customerName : customerNames) {
			try {
				Long createdAccountNo = bankService.createAccount(customerName);
				bankAppLogger.info("Account saved in database with accountNo = " + createdAccountNo + " and Customer = "
						+ customerName);
			} catch (Exception e) {
				bankAppLogger.error("Error while saving account: " + e.getMessage());
			}
		}
	}

	/**
	 * Handler to handle <i>show balance</i> request by user.
	 */
	private void handleShowBalance() {
		try {
			bankAppLogger.debug("Taking user input.");
			System.out.println("Enter Account No. to view balance");
			consoleManager.printInputPrefix();
			Long accountNo = scanner.nextLong();
			bankAppLogger.debug("User Input: " + "[accountNo = " + accountNo + "]");

			bankAppLogger.debug("Calling showBalance() using BankService object.");
			Double balance = bankService.showBalance(accountNo);
			bankAppLogger.debug("Result of showBalance() fetched: " + "[balance = " + balance + "]");

			Map<String, Object> replacements = new HashMap<>();
			replacements.put("<accountNo>", accountNo);
			replacements.put("<balance>", balance);

			bankAppLogger.debug("Printing output to console.");
			consoleManager.printOutputMessage(ConsoleFileConstants.PRINT_SHOWBALANCE_FILEPATH, replacements);
		} catch (AccountNotFoundException e) {
			bankAppLogger.error("Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.SHOW_BALANCE, e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException(ExceptionHandlerConstants.SHOW_BALANCE, e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.SHOW_BALANCE, e);
		}
	}

	/**
	 * Handler to handle <i>deposit amount</i> request by user.
	 */
	private void handleDepositAmount() {
		try {
			bankAppLogger.debug("Taking user input.");
			System.out.println("Enter Account No. to deposit amount:");
			consoleManager.printInputPrefix();
			Long accountNo = scanner.nextLong();
			System.out.println("Enter Deposit Amount:");
			consoleManager.printInputPrefix();
			Double amount = scanner.nextDouble();
			bankAppLogger.debug("User Input: " + "[accountNo = " + accountNo + ", amount = " + amount + "]");

			bankAppLogger.debug("Calling deposit() using BankService object.");
			Double newBalance = bankService.deposit(accountNo, amount);
			bankAppLogger.debug("Result of deposit() fetched: " + "[newBalance = " + newBalance + "]");

			Map<String, Object> replacements = new HashMap<>();
			replacements.put("<amount>", amount);
			replacements.put("<accountNo>", accountNo);
			replacements.put("<newBalance>", newBalance);

			bankAppLogger.debug("Printing output to console.");
			consoleManager.printOutputMessage(ConsoleFileConstants.PRINT_DEPOSIT_FILEPATH, replacements);
		} catch (AccountNotFoundException | InvalidAmountException | InvalidAccountException e) {
			bankAppLogger.error("Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.DEPOSIT_AMOUNT, e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException(ExceptionHandlerConstants.DEPOSIT_AMOUNT, e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.DEPOSIT_AMOUNT, e);
		}
	}

	/**
	 * Handler to handle <i>withdraw amount</i> request by user.
	 */
	private void handleWithdrawAmount() {
		try {
			bankAppLogger.debug("Taking user input.");
			System.out.println("Enter Account No. to withdraw balance:");
			consoleManager.printInputPrefix();
			Long accountNo = scanner.nextLong();
			System.out.println("Enter Withdraw Amount:");
			consoleManager.printInputPrefix();
			Double amount = scanner.nextDouble();
			bankAppLogger.debug("User Input: " + "[accountNo = " + accountNo + ", amount = " + amount + "]");

			bankAppLogger.debug("Calling withdraw() using BankService object.");
			Double newBalance = bankService.withdraw(accountNo, amount);
			bankAppLogger.debug("Result of deposit() fetched: " + "[newBalance = " + newBalance + "]");

			Map<String, Object> replacements = new HashMap<>();
			replacements.put("<amount>", amount);
			replacements.put("<accountNo>", accountNo);
			replacements.put("<newBalance>", newBalance);

			bankAppLogger.debug("Printing output to console.");
			consoleManager.printOutputMessage(ConsoleFileConstants.PRINT_WITHDRAW_FILEPATH, replacements);
		} catch (AccountNotFoundException | InvalidAmountException | InvalidAccountException | AccountBalanceException
				| TransactionFailedException e) {
			bankAppLogger.error("Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.WITHDRAW_AMOUNT, e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException(ExceptionHandlerConstants.WITHDRAW_AMOUNT, e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.WITHDRAW_AMOUNT, e);
		}
	}

	/**
	 * Handler to handle <i>fund transfer</i> request by user.
	 */
	private void handleFundTransfer() {
		try {
			bankAppLogger.debug("Taking user input.");
			System.out.println("Enter Account No. of Sender:");
			consoleManager.printInputPrefix();
			Long fromAccountNo = scanner.nextLong();
			System.out.println("Enter Account No. of Recipient:");
			consoleManager.printInputPrefix();
			Long toAccountNo = scanner.nextLong();
			System.out.println("Enter Transfer Amount:");
			consoleManager.printInputPrefix();
			Double amount = scanner.nextDouble();
			bankAppLogger.debug("User Input: " + "[fromAccountNo = " + fromAccountNo + ", toAccountNo = " + toAccountNo
					+ ", amount = " + amount + "]");

			bankAppLogger.debug("Calling fundTransfer() using BankService object.");
			Boolean fundTransferSuccess = bankService.fundTransfer(fromAccountNo, toAccountNo, amount);
			if (Boolean.TRUE.equals(fundTransferSuccess)) {
				Map<String, Object> replacements = new HashMap<>();
				replacements.put("<amount>", amount);
				replacements.put("<fromAccountNo>", fromAccountNo);
				replacements.put("<toAccountNo>", toAccountNo);
				replacements.put("<fromAccountBalance>", bankService.showBalance(fromAccountNo));
				replacements.put("<toAccountBalance>", bankService.showBalance(toAccountNo));

				bankAppLogger.debug("Printing output to console.");
				consoleManager.printOutputMessage(ConsoleFileConstants.PRINT_FUNDTRANSFER_SUCCESS_FILEPATH,
						replacements);
			} else {
				String error = "Fund Transfer FAILED.";
				bankAppLogger.error("Error: " + error);
				consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH, error);
			}

		} catch (AccountNotFoundException | InvalidAmountException | InvalidAccountException | AccountBalanceException
				| TransactionFailedException | InvalidFundTransferException e) {
			bankAppLogger.error("Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.FUND_TRANSFER, e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException(ExceptionHandlerConstants.FUND_TRANSFER, e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.FUND_TRANSFER, e);
		}
	}

	/**
	 * Handler to handle <i>get all transaction details</i>.
	 */
	private void handleGetAllTransactionDetails() {
		try {
			bankAppLogger.debug("Taking user input.");
			System.out.println("Enter Account No.:");
			consoleManager.printInputPrefix();
			Long accountNo = Long.parseLong(scanner.nextLine());
			bankAppLogger.debug("User Input: " + "[accountNo = " + accountNo + "]");

			bankAppLogger.debug("Calling getAllTransactionDetails() using BankService object.");
			List<Transaction> transactions = bankService.getAllTransactionDetails(accountNo);

			bankAppLogger.debug("Printing output to console.");
			consoleManager.printTransactionDetails(accountNo, transactions);

			scanner.nextLine();
		} catch (NoTransactionsFoundException | AccountNotFoundException e) {
			bankAppLogger.error("Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.GET_ALL_TRANSACTIONS, e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException(ExceptionHandlerConstants.GET_ALL_TRANSACTIONS, e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException(ExceptionHandlerConstants.GET_ALL_TRANSACTIONS, e);
		}
	}

	/**
	 * Run the application instance.
	 */
	public void run() {
		bankAppLogger.info("Invoked run() method for application instance.");
		consoleManager.printTitle();

		Integer choice = null;
		do {
			consoleManager.printMenu();
			consoleManager.printInputPrefix();
			try {
				choice = scanner.nextInt();
			} catch (Exception e) {
				choice = -1;
			}
			scanner.nextLine(); // flush buffer
			switch (choice) {
			case 0: {
				// Exit
				bankAppLogger.info("User interaction: Exit");
				consoleManager.printExit();
				System.exit(0);
				break;
			}
			case 1: {
				// Show Account Balance
				bankAppLogger.info("User interaction: Show Balance");
				handleShowBalance();
				break;
			}
			case 2: {
				// Deposit Amount
				bankAppLogger.info("User interaction: Deposit Amount");
				handleDepositAmount();
				break;
			}
			case 3: {
				// Withdraw Amount
				bankAppLogger.info("User interaction: Withdraw Amount");
				handleWithdrawAmount();
				break;
			}
			case 4: {
				// Fund Transfer
				bankAppLogger.info("User interaction: Fund Transfer");
				handleFundTransfer();
				break;
			}
			case 5: {
				// Show Last 10 Transactions
				bankAppLogger.info("User interaction: Show Last 10 Transactions");
				handleGetAllTransactionDetails();
				break;
			}
			default: {
				bankAppLogger.error("Invalid choice entered by user: " + choice);
				consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
						"Invalid Choice. Try Again!");
				choice = null;
			}
			}
		} while (choice == null || choice != -1);
	}
}
