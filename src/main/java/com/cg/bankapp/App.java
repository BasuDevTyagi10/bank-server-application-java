package com.cg.bankapp;

import java.util.Scanner;

import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.services.BankServiceImpl;
import com.cg.bankapp.services.IBankService;

/**
 * The class is used to build the application's user interface.
 */
public class App {
	public IBankService bankService = new BankServiceImpl();
	public final String APPLICATION_NAME = "BANK SERVER APPLICATION";

	/**
	 * Handler to handle <i>show balance</i> request by user.
	 * 
	 * @param accountNo The account number for which the balance has to be
	 *                  retrieved.
	 */
	public void handleShowBalance(String accountNo) {
		Double balance = bankService.showBalance(accountNo);
		if (balance != null)
			System.out.println("Balance for Account No. [" + accountNo + "] = Rs. " + balance);
	}

	/**
	 * Handler to handle the transaction requests like <i>deposit</i>,
	 * <i>withdraw</i> and <i>transfer</i> within accounts
	 * 
	 * @param transactionType The type of transaction
	 *                        [<code>DEPOSIT, WITHDRAW, TRANSFER</code>].
	 * @param fromAccountNo   The Account from where the transaction starts.
	 * @param toAccountNo     The Account to where the transaction ends.
	 * @param amount          The amount to be transacted.
	 */
	public void handleTransaction(String transactionType, String fromAccountNo, String toAccountNo, Double amount) {
		switch (transactionType) {
		case "DEPOSIT": {
			Double newBalance = bankService.deposit(toAccountNo, amount);
			if (newBalance != null)
				System.out.println("Balance after DEPOSIT = Rs. " + newBalance);
			break;
		}
		case "WITHDRAW": {
			Double newBalance = bankService.withdraw(fromAccountNo, amount);
			if (newBalance != null)
				System.out.println("Balance after WITHDRAW = Rs. " + newBalance);
			break;
		}
		case "TRANSFER": {
			Boolean fundTransferSucess = bankService.fundTransfer(fromAccountNo, toAccountNo, amount);
			if (fundTransferSucess) {
				System.out.println("Fund Transfer SUCCESSFUL!");
				System.out.println("Balance after TRANSFER = Rs. " + bankService.showBalance(fromAccountNo));
				System.out.println("Balance after TRANSFER = Rs. " + bankService.showBalance(toAccountNo));
			} else {
				System.out.println("Fund Transfer FAILED!");
			}
			break;
		}
		default:
			System.err.println("Unexpected value: " + transactionType);
		}
	}

	/**
	 * Handler to handle <i>get all transaction details</i>.
	 * 
	 * @param accountNo The Account number for which the transaction details have to
	 *                  be retrieved.
	 */
	public void handleGetAllTransactionDetails(String accountNo) {
		Transaction[] transactions = bankService.getAllTransactionDetails(accountNo);
		if (transactions == null) {
			System.out.println("NO TRANSACTIONS FROM THIS ACCOUNT NO. " + accountNo + " YET.");
		} else {
			System.out.println("ALL TRANSACTIONS FOR ACCOUNT NUMBER: " + accountNo);
			System.out.println("FROM\t\t\tTO\t\t\tAMOUNT\tTXN ID\t\tTXN TYPE");
			for (Transaction transaction : bankService.getAllTransactionDetails(accountNo)) {
				if (transaction != null)
					System.out.println(transaction.getFromAccount() + "\t\t\t" + transaction.getToAccount() + "\t\t\t"
							+ transaction.getTransactionAmount() + "\t" + transaction.getTransactionId() + "\t"
							+ transaction.getTransactionType() + "\t");
			}
		}
	}

	/**
	 * Handler to handle <i>exit application</i> interaction by user.
	 */
	public void handleExit() {
		System.out.println("Thank you for using XYZ Bank.");
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
	 * Constructs an instance of the BankServerApplication for the user to interact
	 * with.
	 */
	public App() {
		System.out.println(APPLICATION_NAME);
		System.out.println("Welcome User!\n");

		Scanner scanner = new Scanner(System.in);
		try {
			while (true) {
				printMenu();
				Integer choice = scanner.nextInt();
				scanner.nextLine();
				switch (choice) {
				case 0: {
					handleExit();
					break;
				}
				case 1: {
					try {
						System.out.println("Enter Account No.:");
						String accountNo = scanner.nextLine();
						handleShowBalance(accountNo);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					break;
				}
				case 2: {
					try {
						System.out.println("Enter Account No.:");
						String accountNo = scanner.nextLine();
						System.out.println("Enter Amount:");
						Double amount = scanner.nextDouble();
						handleTransaction("DEPOSIT", null, accountNo, amount);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					break;
				}
				case 3: {
					try {
						System.out.println("Enter Account No.:");
						String accountNo = scanner.nextLine();
						System.out.println("Enter Amount:");
						Double amount = scanner.nextDouble();
						handleTransaction("WITHDRAW", accountNo, null, amount);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					break;
				}
				case 4: {
					try {
						System.out.println("Enter Account No. of Sender:");
						String fromAccountNo = scanner.nextLine();
						System.out.println("Enter Account No. of Recipient:");
						String toAccountNo = scanner.nextLine();
						System.out.println("Enter Amount to be transferred:");
						Double amount = scanner.nextDouble();
						handleTransaction("TRANSFER", fromAccountNo, toAccountNo, amount);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					break;
				}
				case 5: {
					try {
						System.out.println("Enter Account No.:");
						String accountNo = scanner.nextLine();
						handleGetAllTransactionDetails(accountNo);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					break;
				}
				default:
					System.err.println("Unexpected value: " + choice);
				}
			}
		} catch (Exception e) {
			System.err.println("SOMETHING WENT WRONG:");
		} finally {
			scanner.close();
		}
	}

	public static void main(String[] args) {
		new App();
	}
}
