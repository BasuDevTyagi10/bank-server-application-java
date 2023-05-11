package com.cg.bankapp;

import java.util.List;
import java.util.Scanner;

import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.DatabaseException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.services.BankServiceImpl;
import com.cg.bankapp.services.IBankService;

/**
 * The class is used to build the application's user interface.
 */
public class App {

	public static final String APPLICATION_NAME = "BANK SERVER APPLICATION";
	public static final String UNEXPECTED_ERROR_MESSAGE = "SOMETHING WENT WRONG";
	public static final String SPACER = "\n\n";
	public static final String SEPARATOR = "**********************************";

	private IBankService bankService = null;
	private Scanner scanner = new Scanner(System.in);

	public App() {
		try {
			bankService = new BankServiceImpl();
		} catch (DatabaseException e) {
			System.err.println("Unable to start the application as database is not loaded properly.");
			System.err.println("More: " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Handler to handle <i>show balance</i> request by user.
	 */
	private void handleShowBalance() {
		try {
			System.out.println(SEPARATOR);
			System.out.println("Enter Account Number to view Balance:");
			Integer accountNo = Integer.parseInt(scanner.nextLine());
			Double balance = bankService.showBalance(accountNo);
			printResult("Balance for Account No. [" + accountNo + "] = Rs. " + balance);
		} catch (AccountNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(UNEXPECTED_ERROR_MESSAGE + ": " + e.getClass());
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Handler to handle the transaction requests like <i>deposit</i>,
	 * <i>withdraw</i> and <i>transfer</i> within accounts
	 * 
	 * @param transactionType The type of transaction
	 *                        [<code>DEPOSIT, WITHDRAW, TRANSFER</code>].
	 */
	private void handleTransaction(String transactionType) {

		switch (transactionType) {
		case "DEPOSIT": {
			try {
				System.out.println("Enter Account Number to deposit amount:");
				Integer accountNo = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter Deposit Amount:");
				Double amount = scanner.nextDouble();
				Double newBalance = bankService.deposit(accountNo, amount);
				printResult(String.format("Rs. %.2f CREDITED from Account No. %s.%n", amount, accountNo),
						"Balance after DEPOSIT = Rs. " + newBalance);
			} catch (AccountNotFoundException | InvalidAmountException | InvalidAccountException e) {
				System.err.println(e.getMessage());
			} catch (Exception e) {
				System.err.println(UNEXPECTED_ERROR_MESSAGE + ":" + e.getCause());
				System.err.println(e.getMessage());
			}
			break;
		}
		case "WITHDRAW": {
			try {
				System.out.println("Enter Account Number to withdraw balance:");
				Integer accountNo = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter Withdraw Amount:");
				Double amount = scanner.nextDouble();
				Double newBalance = bankService.withdraw(accountNo, amount);
				printResult(String.format("Rs. %.2f DEBITED from Account No. %s.%n", amount, accountNo),
						"Balance after WITHDRAW = Rs. " + newBalance);
			} catch (AccountNotFoundException | InvalidAmountException | AccountBalanceException
					| InvalidAccountException e) {
				System.err.println(e.getMessage());
			} catch (Exception e) {
				System.err.println(UNEXPECTED_ERROR_MESSAGE + ":" + e.getCause());
				System.err.println(e.getMessage());
			}
			break;
		}
		case "TRANSFER": {
			try {
				System.out.println("Enter Account No. of Sender:");
				Integer fromAccountNo = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter Account No. of Recipient:");
				Integer toAccountNo = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter Transfer Amount:");
				Double amount = scanner.nextDouble();

				Boolean fundTransferSucess = bankService.fundTransfer(fromAccountNo, toAccountNo, amount);
				if (Boolean.TRUE.equals(fundTransferSucess)) {
					printResult("Fund Transfer SUCCESSFUL!",
							String.format("Rs. %.2f TRANSFERRED from Account No. %s to Account No. %s%n", amount,
									fromAccountNo, toAccountNo),
							"Balance after TRANSFER in " + fromAccountNo + " = Rs. "
									+ bankService.showBalance(fromAccountNo),
							"Balance after TRANSFER in " + toAccountNo + " = Rs. "
									+ bankService.showBalance(toAccountNo));
				}
			} catch (InvalidFundTransferException | AccountNotFoundException | AccountBalanceException
					| InvalidAmountException | InvalidAccountException e) {
				System.err.println("Fund Transfer FAILED!");
				System.err.println(e.getMessage());
			} catch (Exception e) {
				System.err.println(UNEXPECTED_ERROR_MESSAGE + ":" + e.getCause());
				System.err.println(e.getMessage());
			}
			break;
		}
		default:
			System.err.println("Unexpected value: " + transactionType);
		}
	}

	/**
	 * Handler to handle <i>get all transaction details</i>.
	 */
	private void handleGetAllTransactionDetails() {
		try {
			System.out.println("Enter Account No.:");
			Integer accountNo = Integer.parseInt(scanner.nextLine());

			List<Transaction> transactions = bankService.getAllTransactionDetails(accountNo);

			System.out.println("\nRESULT:");
			System.out.println(SEPARATOR + SEPARATOR);
			System.out.println("ALL TRANSACTIONS FOR ACCOUNT NUMBER: " + accountNo);
			System.out.println("FROM\t\tTO\t\tAMOUNT\t\tTXN ID\t\tTXN TYPE");
			for (Transaction transaction : transactions) {
				String fromAccount = resolveAccount(transaction.getFromAccount(), accountNo);
				String toAccount = resolveAccount(transaction.getToAccount(), accountNo);

				System.out.println(fromAccount + "\t\t" + toAccount + "\t\t" + transaction.getTransactionAmount()
						+ "\t\t" + transaction.getTransactionId() + "\t\t" + transaction.getTransactionType());
			}
			System.out.println("\n" + SEPARATOR + SEPARATOR);
			scanner.nextLine();
		} catch (NoTransactionsFoundException e) {
			printResult(e.getMessage());
		} catch (AccountNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(UNEXPECTED_ERROR_MESSAGE + ":" + e.getCause());
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Handler to resolve the fromAccount and toAccount for printing on console.
	 * 
	 * @param getValue
	 * @param compareValue
	 * @return The resolved value as String to print on console.
	 */
	private String resolveAccount(Integer getValue, Integer compareValue) {
		String resolvedValue = null;
		if (getValue == null) {
			resolvedValue = "-";
		} else {
			if (getValue.equals(compareValue)) {
				resolvedValue = "SELF";
			} else {
				resolvedValue = getValue.toString();
			}
		}
		return resolvedValue;
	}

	/**
	 * Handler to handle <i>exit application</i> interaction by user.
	 */
	private void handleExit() {
		System.out.println("Thank you for using " + APPLICATION_NAME + ".");
		System.exit(0);
	}

	/**
	 * Handler to print the Main Menu of the application.
	 */
	private void printMenu() {
		System.out.println(SPACER);
		System.out.println(SEPARATOR);
		System.out.println("MAIN MENU");
		System.out.println("> 0: Exit");
		System.out.println("> 1. Show Account Balance");
		System.out.println("> 2. Deposit Amount");
		System.out.println("> 3. Withdraw Amount");
		System.out.println("> 4. Transfer Funds");
		System.out.println("> 5. Show All Transaction Details");
		System.out.println(SEPARATOR);
		System.out.print("Enter your choice: ");
	}

	/**
	 * Handler to print the Result of the action performed by user.
	 * 
	 * @param resultMessage Result lines to be printed.
	 */
	private void printResult(String... resultMessage) {
		System.out.println("\nRESULT:");
		System.out.println(SEPARATOR);
		for (String resultLine : resultMessage) {
			System.out.println(resultLine);
		}
		System.out.println(SEPARATOR);
	}

	/**
	 * Run the application instance.
	 */
	public void run() {
		System.out.println(SPACER);
		System.out.println(APPLICATION_NAME);
		System.out.println("Welcome User!");

		Integer choice = null;
		do {
			printMenu();
			try {
				choice = scanner.nextInt();
				scanner.nextLine(); // flush buffer
			} catch (Exception e) {
				scanner.nextLine(); // flush buffer
				choice = -1;
			}
			switch (choice) {
			case 0: {
				// Exit
				handleExit();
				break;
			}
			case 1: {
				// Show Account Balance
				handleShowBalance();
				break;
			}
			case 2: {
				// Deposit Amount
				handleTransaction("DEPOSIT");
				break;
			}
			case 3: {
				// Withdraw Amount
				handleTransaction("WITHDRAW");
				break;
			}
			case 4: {
				// Fund Transfer
				handleTransaction("TRANSFER");
				break;
			}
			case 5: {
				// Show Last 10 Transactions
				handleGetAllTransactionDetails();
				break;
			}
			default:
				System.err.println("Invalid Choice. Try Again!");
				choice = null;
			}
		} while (choice == null || choice != -1);
	}
}
