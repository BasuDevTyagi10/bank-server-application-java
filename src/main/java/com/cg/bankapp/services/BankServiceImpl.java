package com.cg.bankapp.services;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.ETransactionType;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.dao.BankDAOImpl;
import com.cg.bankapp.dao.IBankDAO;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.DataNotFoundException;
import com.cg.bankapp.exceptions.TransactionLimitReachedException;

/**
 * This class implements <code>IBankService</code> to interact with Bank DAO by
 * overriding the methods in the parent interface.
 */
public class BankServiceImpl implements IBankService {
	private IBankDAO bankDAO = new BankDAOImpl();

	/**
	 * Helper method to check if an <code>Account</code> has reached its transaction
	 * limit.
	 * 
	 * @param account The <code>Account</code> for which the limit needs to be
	 *                checked.
	 * @return true if transaction limit is not reached.
	 * @throws TransactionLimitReachedException
	 */
	private Boolean checkTransactionLimit(Account account) throws TransactionLimitReachedException {
		if (account.noOfTransactions() == Transaction.getTxnLimit()) {
			throw new TransactionLimitReachedException("Account Number: " + account.getAccountNo()
					+ " has reached the limit of " + Transaction.getTxnLimit() + " transactions.");
		} else {
			return true;
		}
	}

	/**
	 * Helper method to add a newly created transaction in the <code>Account</code>
	 * <code>Transaction</code> array.
	 * 
	 * @param account     The <code>Account</code> object in which the
	 *                    <code>Transaction</code> object needs to be added.
	 * @param transaction The <code>Transaction</code> object which will be added.
	 * @throws TransactionLimitReachedException
	 */
	private void addTransactionToHistory(Account account, Transaction transaction) {
		try {
			checkTransactionLimit(account);
			// Stores transactions in latest to oldest order.
			for (Integer index = account.getTransactionArrayIndex(); index > 0; index--) {
				account.getTransactions()[index] = account.getTransactions()[index - 1];
			}
			account.getTransactions()[0] = transaction;
			account.setTransactionArrayIndex(account.getTransactionArrayIndex() + 1);
		} catch (TransactionLimitReachedException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public Double showBalance(String accountNo) {
		try {
			Account account = bankDAO.getAccountById(accountNo);
			return account.getAccountBalance();
		} catch (DataNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public Double deposit(String accountNo, Double amount) {
		Account account = null;
		Double balance = null;
		try {
			account = bankDAO.getAccountById(accountNo);
			balance = account.getAccountBalance();
		} catch (DataNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Deposit amount cannot be negative or zero.");
		}
		try {
			checkTransactionLimit(account);
		} catch (TransactionLimitReachedException e) {
			System.err.println(e.getMessage());
			return null;
		}
		balance += amount;
		account.setAccountBalance(balance);

		Transaction transaction = new Transaction(ETransactionType.CREDIT, null, "SELF", amount);
		addTransactionToHistory(account, transaction);
		System.out.printf("Rs. %.2f CREDITED to Account No. %s.\n", amount, account.getAccountNo());
		return account.getAccountBalance();
	}

	@Override
	public Double withdraw(String accountNo, Double amount) {
		Account account = null;
		Double balance = null;
		try {
			account = bankDAO.getAccountById(accountNo);
			balance = account.getAccountBalance();
		} catch (DataNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Withdraw amount cannot be negative or zero.");
		}
		try {
			checkTransactionLimit(account);
		} catch (TransactionLimitReachedException e) {
			System.err.println(e.getMessage());
			return null;
		}
		try {
			if (amount > balance) {
				throw new AccountBalanceException();
			}
			balance -= amount;
			account.setAccountBalance(balance);

			Transaction transaction = new Transaction(ETransactionType.DEBIT, "SELF", null, amount);
			addTransactionToHistory(account, transaction);
			System.out.printf("Rs. %.2f DEBITED from Account No. %s.\n", amount, account.getAccountNo());
			return account.getAccountBalance();
		} catch (AccountBalanceException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public Boolean fundTransfer(String fromAccountNo, String targetAccountNo, Double amount) {
		Account fromAccount = null;
		Account targetAccount = null;
		Double fromBalance = null;
		Double targetBalance = null;
		try {
			fromAccount = bankDAO.getAccountById(fromAccountNo);
			fromBalance = fromAccount.getAccountBalance();
			targetAccount = bankDAO.getAccountById(targetAccountNo);
			targetBalance = targetAccount.getAccountBalance();
		} catch (DataNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Transfer amount cannot be negative or zero.");
		}
		try {
			if (amount > fromBalance) {
				throw new AccountBalanceException();
			}
		} catch (AccountBalanceException e) {
			System.err.println(e.getMessage());
			return false;
		}
		try {
			checkTransactionLimit(fromAccount);
			checkTransactionLimit(targetAccount);
		} catch (TransactionLimitReachedException e) {
			System.err.println(e.getMessage());
			return false;
		}
		fromBalance -= amount;
		fromAccount.setAccountBalance(fromBalance);
		targetBalance += amount;
		targetAccount.setAccountBalance(targetBalance);

		Transaction transaction = new Transaction(ETransactionType.TRANSFER, fromAccountNo, targetAccountNo, amount);
		addTransactionToHistory(fromAccount, transaction);
		addTransactionToHistory(targetAccount, transaction);
		System.out.printf("Rs. %.2f TRANSFERRED from Account No. %s to Account No. %s\n", amount, fromAccountNo,
				targetAccountNo);
		return true;
	}

	@Override
	public Transaction[] getAllTransactionDetails(String accountNo) {
		Account account = null;
		try {
			account = bankDAO.getAccountById(accountNo);
			if (account.getTransactionArrayIndex() != 0) {
				return account.getTransactions();
			} else {
				return null;
			}
		} catch (DataNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

}
