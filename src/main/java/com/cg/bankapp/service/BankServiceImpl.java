package com.cg.bankapp.service;

import java.util.List;
import java.util.Optional;

import com.cg.bankapp.dao.BankDAOImpl;
import com.cg.bankapp.dao.IBankDAO;
import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.entity.TransactionType;
import com.cg.bankapp.exception.AccountBalanceException;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.InvalidAccountException;
import com.cg.bankapp.exception.InvalidAmountException;
import com.cg.bankapp.exception.InvalidFundTransferException;
import com.cg.bankapp.exception.NoTransactionsFoundException;
import com.cg.bankapp.exception.TransactionFailedException;

/**
 * This class implements <code>IBankService</code> to interact with the
 * <code>IBankDAO</code> for handling user interactions on the services by
 * overriding the methods in the parent interface.
 */
public class BankServiceImpl implements IBankService {
	private IBankDAO bankDAO = null;

	/**
	 * Constructor to initialize the instance with the IBankDAO object.
	 */
	public BankServiceImpl() {
		bankDAO = new BankDAOImpl();
	}

	/**
	 * Constructor to initialize the instance with a test object of IBankDAO for
	 * testing purpose.
	 * 
	 * @param testBankDAO A test instance of IBankDAO
	 */
	protected BankServiceImpl(IBankDAO testBankDAO) {
		this.bankDAO = testBankDAO;
	}

	@Override
	public Double showBalance(Long accountNo) throws AccountNotFoundException, TransactionFailedException {
		Account account = bankDAO.getAccountById(accountNo);
		return Optional.ofNullable(account.getAccountBalance()).orElse(0.0);
	}

	@Override
	public Double deposit(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			InvalidAccountException, TransactionFailedException {
		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Deposit amount cannot be negative or zero.");

		Account account = bankDAO.getAccountById(accountNo);

		bankDAO.performTransaction(account, TransactionType.CREDIT, amount);

		return account.getAccountBalance();
	}

	@Override
	public Double withdraw(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, InvalidAccountException, TransactionFailedException {
		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Withdraw amount cannot be negative or zero.");

		Account account = bankDAO.getAccountById(accountNo);

		if (account.getAccountBalance() < amount) {
			throw new AccountBalanceException();
		}

		bankDAO.performTransaction(account, TransactionType.DEBIT, amount);

		return account.getAccountBalance();

	}

	@Override
	public Boolean fundTransfer(Long fromAccountNo, Long toAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, InvalidAccountException, TransactionFailedException {

		if (fromAccountNo == null || toAccountNo == null)
			throw new AccountNotFoundException();

		if (fromAccountNo.equals(toAccountNo))
			throw new InvalidFundTransferException("Cannot transfer funds to the same account number");

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Transfer amount cannot be negative or zero.");

		Account fromAccount = bankDAO.getAccountById(fromAccountNo);
		Account toAccount = bankDAO.getAccountById(toAccountNo);

		if (fromAccount.getAccountBalance() < amount) {
			throw new AccountBalanceException();
		}

		bankDAO.performTransaction(fromAccount, toAccount, amount);

		return true;
	}

	@Override
	public List<Transaction> getAllTransactionDetails(Long accountNo)
			throws AccountNotFoundException, NoTransactionsFoundException, TransactionFailedException {
		Account account = bankDAO.getAccountById(accountNo);
		List<Transaction> transactions = account.getTransactions();

		if (transactions == null || transactions.isEmpty()) {
			throw new NoTransactionsFoundException();
		}

		return transactions;
	}
}
