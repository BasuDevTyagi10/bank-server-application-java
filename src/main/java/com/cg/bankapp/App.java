package com.cg.bankapp;

import java.util.List;
import java.util.Scanner;

import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.services.BankServiceImpl;
import com.cg.bankapp.services.IBankService;

/**
 * The class is used to build the application's user interface.
 */
public class App {
	IBankService bankService = new BankServiceImpl();
	public static final String APPLICATION_NAME = "BANK SERVER APPLICATION";
	public static final String UNEXPECTED_ERROR_MESSAGE = "SOMETHING WENT WRONG";
	private Scanner scanner = new Scanner(System.in);

	/**
	 * Handler to handle <i>show balance</i> request by user.
	 */
	public void handleShowBalance() {
		try {
			System.out.println("Enter Account Number to view Balance:");
			String accountNo = scanner.nextLine();
			Double balance = bankService.showBalance(accountNo);
			System.out.println("Balance for Account No. [" + accountNo + "] = Rs. " + balance);
		} catch (AccountNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(UNEXPECTED_ERROR_MESSAGE);
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
	public void handleTransaction(String transactionType) {

		switch (transactionType) {
		case "DEPOSIT": {
			try {
				System.out.println("Enter Account Number to deposit amount:");
				String accountNo = scanner.nextLine();
				System.out.println("Enter Deposit Amount:");
				Double amount = scanner.nextDouble();
				Double newBalance = bankService.deposit(accountNo, amount);
				System.out.printf("Rs. %.2f CREDITED to Account No. %s.%n", amount, accountNo);
				System.out.println("Balance after DEPOSIT = Rs. " + newBalance);
			} catch (AccountNotFoundException | InvalidAmountException e) {
				System.err.println(e.getMessage());
			} catch (Exception e) {
				System.err.println(UNEXPECTED_ERROR_MESSAGE);
				System.err.println(e.getMessage());
			}
			break;
		}
		case "WITHDRAW": {
			try {
				System.out.println("Enter Account Number to withdraw balance:");
				String accountNo = scanner.nextLine();
				System.out.println("Enter Withdraw Amount:");
				Double amount = scanner.nextDouble();
				Double newBalance = bankService.withdraw(accountNo, amount);
				System.out.printf("Rs. %.2f DEBITED from Account No. %s.%n", amount, accountNo);
				System.out.println("Balance after WITHDRAW = Rs. " + newBalance);
			} catch (AccountNotFoundException | InvalidAmountException | AccountBalanceException e) {
				System.err.println(e.getMessage());
			} catch (Exception e) {
				System.err.println(UNEXPECTED_ERROR_MESSAGE);
				System.err.println(e.getMessage());
			}
			break;
		}
		case "TRANSFER": {
			try {
				System.out.println("Enter Account No. of Sender:");
				String fromAccountNo = scanner.nextLine();
				System.out.println("Enter Account No. of Recipient:");
				String toAccountNo = scanner.nextLine();
				System.out.println("Enter Transfer Amount:");
				Double amount = scanner.nextDouble();

				Boolean fundTransferSucess = bankService.fundTransfer(fromAccountNo, toAccountNo, amount);
				if (Boolean.TRUE.equals(fundTransferSucess)) {
					System.out.println("Fund Transfer SUCCESSFUL!");
					System.out.printf("Rs. %.2f TRANSFERRED from Account No. %s to Account No. %s%n", amount,
							fromAccountNo, toAccountNo);
					System.out.println("Balance after TRANSFER in " + fromAccountNo + "= Rs. "
							+ bankService.showBalance(fromAccountNo));
					System.out.println(
							"Balance after TRANSFER = " + toAccountNo + "Rs. " + bankService.showBalance(toAccountNo));
				}
			} catch (InvalidFundTransferException | AccountNotFoundException | AccountBalanceException
					| InvalidAmountException e) {
				System.out.println("Fund Transfer FAILED!");
				System.err.println(e.getMessage());
			} catch (Exception e) {
				System.err.println(UNEXPECTED_ERROR_MESSAGE);
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
	public void handleGetAllTransactionDetails() {
		try {
			System.out.println("Enter Account No.:");
			String accountNo = scanner.nextLine();

			List<Transaction> transactions = bankService.getAllTransactionDetails(accountNo);
			System.out.println("ALL TRANSACTIONS FOR ACCOUNT NUMBER: " + accountNo);
			System.out.println("FROM\t\t\tTO\t\t\tAMOUNT\tTXN ID\t\tTXN TYPE");
			for (Transaction transaction : transactions) {
				System.out.println(transaction.getFromAccount() + "\t\t\t" + transaction.getToAccount() + "\t\t\t"
						+ transaction.getTransactionAmount() + "\t" + transaction.getTransactionId() + "\t"
						+ transaction.getTransactionType() + "\t");
			}
		} catch (AccountNotFoundException | NoTransactionsFoundException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(UNEXPECTED_ERROR_MESSAGE);
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Handler to handle <i>exit application</i> interaction by user.
	 */
	public void handleExit() {
		System.out.println("Thank you for using CG Bank.");
		System.exit(0);
	}

	/**
	 * Handler to print the Main Menu of the application.
	 */
	public void printMenu() {
		System.out.println("\nMAIN MENU");
		System.out.println("> 0: Exit");
		System.out.println("> 1. Show Account Balance");
		System.out.println("> 2. Deposit Amount");
		System.out.println("> 3. Withdraw Amount");
		System.out.println("> 4. Transfer Funds");
		System.out.println("> 5. Show All Transaction Details");
		System.out.println("**********************************");
		System.out.print("Enter your choice: ");
	}

	/**
	 * Run the application instance.
	 */
	public void run() {
		System.out.println(APPLICATION_NAME);
		System.out.println("Welcome User!\n");
		Integer choice = null;
		while (choice == null || choice != -1) {
			printMenu();
			try {
				choice = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.err.println("Invalid Choice: " + choice);
				continue;
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
				System.err.println("Invalid Choice: " + choice);
			}
		}
	}

	public static void main(String[] args) {
		new App().run();
	}
}
