package com.cg.bankapp.services;

import java.util.List;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.ETransactionType;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.dao.BankDAOImpl;
import com.cg.bankapp.dao.IBankDAO;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.exceptions.TransactionUpdationException;

/**
 * This class implements <code>IBankService</code> to interact with Bank DAO by
 * overriding the methods in the parent interface.
 */
public class BankServiceImpl implements IBankService {
	IBankDAO bankDAO = new BankDAOImpl();

	/**
	 * Helper method to add a newly created transaction in the <code>Account</code>
	 * <code>Transaction</code> array.
	 * 
	 * @param account     The <code>Account</code> object in which the
	 *                    <code>Transaction</code> object needs to be added.
	 * @param transaction The <code>Transaction</code> object which will be added.
	 * @throws TransactionLimitReachedException
	 */
	private Boolean addTransactionToHistory(Account account, Transaction transaction) {
		try {
			account.getTransactions().add(0, transaction);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Double showBalance(String accountNo) throws AccountNotFoundException {
		Account account = bankDAO.getAccountById(accountNo);
		return account.getAccountBalance();
	}

	@Override
	public Double deposit(String accountNo, Double amount)
			throws AccountNotFoundException, InvalidAmountException, TransactionUpdationException {
		Account account = bankDAO.getAccountById(accountNo);
		Double balance = account.getAccountBalance();
		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Deposit amount cannot be negative or zero.");

		Transaction transaction = new Transaction(ETransactionType.CREDIT, null, "SELF", amount);
		if (Boolean.TRUE.equals(addTransactionToHistory(account, transaction))) {
			balance += amount;
			account.setAccountBalance(balance);
		} else {
			throw new TransactionUpdationException();
		}
		return account.getAccountBalance();
	}

	@Override
	public Double withdraw(String accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, TransactionUpdationException {
		Account account = bankDAO.getAccountById(accountNo);
		Double balance = account.getAccountBalance();

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Withdraw amount cannot be negative or zero.");

		if (amount > balance)
			throw new AccountBalanceException();

		Transaction transaction = new Transaction(ETransactionType.DEBIT, "SELF", null, amount);
		if (Boolean.TRUE.equals(addTransactionToHistory(account, transaction))) {
			balance -= amount;
			account.setAccountBalance(balance);
		} else {
			throw new TransactionUpdationException();
		}
		return account.getAccountBalance();
	}

	@Override
	public Boolean fundTransfer(String fromAccountNo, String targetAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, TransactionUpdationException {

		if (fromAccountNo == null || targetAccountNo == null)
			throw new AccountNotFoundException();

		if (fromAccountNo.equals(targetAccountNo))
			throw new InvalidFundTransferException("Cannot transfer funds to the same account number");

		Account fromAccount = bankDAO.getAccountById(fromAccountNo);
		Double fromBalance = fromAccount.getAccountBalance();
		Account targetAccount = bankDAO.getAccountById(targetAccountNo);
		Double targetBalance = targetAccount.getAccountBalance();

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Transfer amount cannot be negative or zero.");

		if (amount > fromBalance)
			throw new AccountBalanceException();

		Transaction transaction = new Transaction(ETransactionType.TRANSFER, fromAccountNo, targetAccountNo, amount);
		if (Boolean.TRUE.equals(addTransactionToHistory(fromAccount, transaction))
				&& Boolean.TRUE.equals(addTransactionToHistory(targetAccount, transaction))) {
			fromBalance -= amount;
			fromAccount.setAccountBalance(fromBalance);
			targetBalance += amount;
			targetAccount.setAccountBalance(targetBalance);
		} else {
			throw new TransactionUpdationException();
		}
		return true;
	}

	@Override
	public List<Transaction> getAllTransactionDetails(String accountNo)
			throws AccountNotFoundException, NoTransactionsFoundException {
		Account account = bankDAO.getAccountById(accountNo);
		List<Transaction> transactions = account.getTransactions();
		if (transactions == null || transactions.isEmpty()) {
			throw new NoTransactionsFoundException();
		}

		return transactions.subList(0, Math.min(10, transactions.size()));
	}

}
