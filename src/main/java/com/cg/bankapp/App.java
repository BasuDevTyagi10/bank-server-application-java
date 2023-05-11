package com.cg.bankapp;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.exception.AccountBalanceException;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.InvalidAccountException;
import com.cg.bankapp.exception.InvalidAmountException;
import com.cg.bankapp.exception.InvalidFundTransferException;
import com.cg.bankapp.exception.NoTransactionsFoundException;
import com.cg.bankapp.exception.TransactionFailedException;
import com.cg.bankapp.service.BankServiceImpl;
import com.cg.bankapp.service.IBankService;
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
	public static final String EXCEPTION_HANDLER_CONFIG_XML = "src/main/resources/exception-handler.xml";

	private IBankService bankService;
	private Scanner scanner = new Scanner(System.in);
	private ConsoleManager consoleManager = new ConsoleManager(APPLICATION_NAME);

	private BankAppLogger bankAppLogger = new BankAppLogger(getClass());
	private ExceptionHandler exceptionHandler = new ExceptionHandler(EXCEPTION_HANDLER_CONFIG_XML);

	/**
	 * Constructor to initialize the application.
	 */
	public App() {
		bankAppLogger.info("Initialized application instance for: " + APPLICATION_NAME);
		bankService = new BankServiceImpl();
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

			bankAppLogger.debug("Calling showBalance() using IBankService object.");
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
			exceptionHandler.handleException("ShowBalance", e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException("ShowBalance", e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException("ShowBalance", e);
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

			bankAppLogger.debug("Calling deposit() using IBankService object.");
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
			exceptionHandler.handleException("DepositAmount", e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException("DepositAmount", e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException("DepositAmount", e);
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

			bankAppLogger.debug("Calling withdraw() using IBankService object.");
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
			exceptionHandler.handleException("WithdrawAmount", e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException("WithdrawAmount", e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException("WithdrawAmount", e);
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

			bankAppLogger.debug("Calling fundTransfer() using IBankService object.");
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
			exceptionHandler.handleException("FundTransfer", e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException("FundTransfer", e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException("FundTransfer", e);
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

			bankAppLogger.debug("Calling getAllTransactionDetails() using IBankService object.");
			List<Transaction> transactions = bankService.getAllTransactionDetails(accountNo);

			bankAppLogger.debug("Printing output to console.");
			consoleManager.printTransactionDetails(accountNo, transactions);

			scanner.nextLine();
		} catch (NoTransactionsFoundException | AccountNotFoundException e) {
			bankAppLogger.error("Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException("GetAllTransactions", e);
		} catch (InputMismatchException e) {
			scanner.nextLine();
			bankAppLogger.error("Input Mismatch Error: " + e.getMessage());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_ERROR_FILEPATH,
					"Given account number is in invalid format.");
			exceptionHandler.handleException("GetAllTransactions", e);
		} catch (Exception e) {
			bankAppLogger.fatal("Unexpected Error: " + e.getStackTrace());
			consoleManager.printErrorMessage(ConsoleFileConstants.PRINT_UNEXPECTED_ERROR_FILEPATH, e.getMessage());
			exceptionHandler.handleException("GetAllTransactions", e);
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
