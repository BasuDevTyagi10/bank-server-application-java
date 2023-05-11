package com.cg.bankapp.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.ETransactionType;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.dao.BankDAOImpl;
import com.cg.bankapp.dao.IBankDAO;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.DatabaseConnectionException;
import com.cg.bankapp.exceptions.DatabaseException;
import com.cg.bankapp.exceptions.DatabaseQueryException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.util.BankDatabase;

/**
 * This class implements <code>IBankService</code> to interact with Bank DAO by
 * overriding the methods in the parent interface.
 */
public class BankServiceImpl implements IBankService {
	private IBankDAO bankDAO = null;

	public BankServiceImpl() throws DatabaseException {
		BankDatabase bankDatabase = new BankDatabase();
		try {
			bankDAO = new BankDAOImpl(bankDatabase.getDatabaseConnection());
		} catch (DatabaseConnectionException e) {
			throw new DatabaseException("Service unavailable due to Database Error: " + e.getMessage());
		}
	}

	protected BankServiceImpl(IBankDAO testBankDAO) {
		this.bankDAO = testBankDAO;
	}

	@Override
	public Double showBalance(Integer accountNo) throws AccountNotFoundException, DatabaseException {
		try {
			Account account = bankDAO.getAccountById(accountNo);
			return Optional.ofNullable(account.getAccountBalance()).orElse(0D);
		} catch (AccountNotFoundException e) {
			throw e;
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public Double deposit(Integer accountNo, Double amount)
			throws AccountNotFoundException, InvalidAmountException, DatabaseException, InvalidAccountException {
		Account account = null;

		try {
			account = bankDAO.getAccountById(accountNo);
		} catch (AccountNotFoundException e) {
			throw e;
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		}

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Deposit amount cannot be negative or zero.");

		Double balance = account.getAccountBalance();
		Double newBalance = balance;

		try {
			bankDAO.createTransaction(new Transaction(ETransactionType.CREDIT, null, accountNo, amount));
			account.setAccountBalance(balance + amount);
			newBalance = bankDAO.updateBalance(account);
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		} catch (InvalidAccountException e) {
			throw e;
		}

		return newBalance;
	}

	@Override
	public Double withdraw(Integer accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, DatabaseException, InvalidAccountException {
		Account account = null;
		try {
			account = bankDAO.getAccountById(accountNo);
		} catch (AccountNotFoundException e) {
			throw e;
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		}

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Withdraw amount cannot be negative or zero.");

		Double balance = account.getAccountBalance();
		Double newBalance = null;

		if (amount > balance)
			throw new AccountBalanceException();

		try {
			bankDAO.createTransaction(new Transaction(ETransactionType.DEBIT, accountNo, null, amount));
			account.setAccountBalance(balance - amount);
			newBalance = bankDAO.updateBalance(account);
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		} catch (InvalidAccountException e) {
			throw e;
		}

		return newBalance;
	}

	@Override
	public Boolean fundTransfer(Integer fromAccountNo, Integer targetAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, DatabaseException, InvalidAccountException {

		if (fromAccountNo == null || targetAccountNo == null)
			throw new AccountNotFoundException();

		if (fromAccountNo.equals(targetAccountNo))
			throw new InvalidFundTransferException("Cannot transfer funds to the same account number");

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Transfer amount cannot be negative or zero.");

		Account fromAccount = null;
		Account toAccount = null;
		try {
			fromAccount = bankDAO.getAccountById(fromAccountNo);
			toAccount = bankDAO.getAccountById(targetAccountNo);
		} catch (AccountNotFoundException e) {
			throw e;
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		}

		Double fromAccountBalance = fromAccount.getAccountBalance();

		Boolean isFundTransferSuccess = false;

		if (amount > fromAccountBalance)
			throw new AccountBalanceException();

		Double targetAccountBalance = toAccount.getAccountBalance();

		try {
			bankDAO.createTransaction(
					new Transaction(ETransactionType.TRANSFER, fromAccountNo, targetAccountNo, amount));
			fromAccount.setAccountBalance(fromAccountBalance - amount);
			bankDAO.updateBalance(fromAccount);
			toAccount.setAccountBalance(targetAccountBalance + amount);
			bankDAO.updateBalance(toAccount);
			isFundTransferSuccess = true;
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		} catch (InvalidAccountException e) {
			throw e;
		}

		return isFundTransferSuccess;
	}

	@Override
	public List<Transaction> getAllTransactionDetails(Integer accountNo)
			throws AccountNotFoundException, NoTransactionsFoundException, DatabaseException {
		Account account = null;

		try {
			account = bankDAO.getAccountById(accountNo);
		} catch (AccountNotFoundException e) {
			throw e;
		} catch (DatabaseConnectionException | DatabaseQueryException e) {
			throw new DatabaseException(e.getMessage());
		}

		List<Transaction> transactions = account.getTransactions();
		if (transactions == null || transactions.isEmpty()) {
			throw new NoTransactionsFoundException();
		}

		return transactions.stream().sorted(Comparator.comparing(Transaction::getTransactionDatetime).reversed())
				.limit(10).collect(Collectors.toList());
	}
}
