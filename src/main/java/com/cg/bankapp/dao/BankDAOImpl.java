package com.cg.bankapp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.entity.TransactionType;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.TransactionFailedException;
import com.cg.bankapp.util.BankDatabaseConstants;
import com.cg.bankapp.util.logging.BankAppLogger;

@Repository
public class BankDAOImpl implements BankDAO {

	@PersistenceContext
	EntityManager entityManager;

	private BankAppLogger bankAppLogger = new BankAppLogger(getClass());

	public BankDAOImpl() {
	}

	protected BankDAOImpl(EntityManager testEntityManager) {
		this.entityManager = testEntityManager;
	}

	@Override
	public Long save(Account account) throws InvalidAccountException, TransactionFailedException {
		Long generatedAccountNo = null;

		bankAppLogger.info("Saving account: " + account);
		if (account == null) {
			String error = "Cannot save a null Account object.";
			bankAppLogger.error(error);
			throw new InvalidAccountException(error);
		}
		if (account.getCustomer() == null) {
			String error = "Cannot save an Account object without a Customer.";
			bankAppLogger.error(error);
			throw new InvalidAccountException(error);
		}

		bankAppLogger.debug("Persisting data: " + account);
		try {
			entityManager.persist(account);
			bankAppLogger.info("Data persisted: " + account);
			generatedAccountNo = account.getAccountNo();
		} catch (Exception e) {
			throw new TransactionFailedException(e.getMessage());
		}

		return generatedAccountNo;
	}

	@Override
	public Account getAccountById(Long accountNo) throws AccountNotFoundException, TransactionFailedException {
		bankAppLogger.info("Fetching account for accountNo: " + accountNo);

		if (accountNo == null || accountNo <= 0) {
			String error = "Invalid account number.";
			bankAppLogger.error(error);
			throw new AccountNotFoundException(error);
		}

		Account fetchedAccount = null;

		try {
			fetchedAccount = entityManager.find(Account.class, accountNo);
			if (fetchedAccount == null) {
				String error = "Account does not exist.";
				bankAppLogger.error(error);
				throw new AccountNotFoundException(error);
			}

			bankAppLogger.debug("Fetching last 10 transactions of the fetched account using NamedQuery");
			TypedQuery<Transaction> query = entityManager.createNamedQuery(
					BankDatabaseConstants.FIND_TRANSACTIONS_BY_ACCOUNT_NO_IDENTIFIER, Transaction.class);
			query.setParameter("accountNo", accountNo);
			query.setMaxResults(10); // Fetch only 10 transactions.
			List<Transaction> transactions = query.getResultList();
			bankAppLogger.debug("Last 10 transactions fetched. Setting account.");

			fetchedAccount.setTransactions(transactions);
		} catch (AccountNotFoundException e) {
			throw e;
		} catch (Exception e) {
			String error = e.getMessage();
			bankAppLogger.error("Fetching Failed: " + error);
			throw new TransactionFailedException(error);
		}

		return fetchedAccount;
	}

	@Override
	public Long performTransaction(Account account, TransactionType transactionType, Double amount)
			throws TransactionFailedException {
		bankAppLogger.info("Performing bank transction [" + transactionType + "] on account: " + account);

		Transaction transaction = new Transaction();

		bankAppLogger.debug("Beginning transaction...");

		if (transactionType == TransactionType.CREDIT) {
			transaction.setTransactionType(transactionType);
			transaction.setToAccount(account);
			transaction.setTransactionAmount(amount);
			try {
				bankAppLogger.debug("Persisting data: " + transaction);
				entityManager.persist(transaction);

				account.setAccountBalance(account.getAccountBalance() + amount);
				bankAppLogger.debug("Persisting data: " + account);
				entityManager.persist(account);

				bankAppLogger.info("Data persisted: " + transaction);
				bankAppLogger.info("Data persisted: " + account);
			} catch (Exception e) {
				throw new TransactionFailedException(e.getMessage());
			}
		} else if (transactionType == TransactionType.DEBIT) {
			transaction.setTransactionType(transactionType);
			transaction.setFromAccount(account);
			transaction.setTransactionAmount(amount);
			try {
				bankAppLogger.debug("Persisting data: " + transaction);
				entityManager.persist(transaction);

				account.setAccountBalance(account.getAccountBalance() - amount);
				bankAppLogger.debug("Persisting data: " + account);
				entityManager.persist(account);

				bankAppLogger.info("Data persisted: " + transaction);
				bankAppLogger.info("Data persisted: " + account);
			} catch (Exception e) {
				throw new TransactionFailedException(e.getMessage());
			}
		}

		return transaction.getTransactionId();
	}

	@Override
	public Long performTransaction(Account fromAccount, Account toAccount, Double amount)
			throws TransactionFailedException {
		bankAppLogger.info("Performing bank transction [" + TransactionType.TRANSFER + "] on account: " + fromAccount
				+ " and " + toAccount);

		Transaction transaction = new Transaction();
		transaction.setTransactionType(TransactionType.TRANSFER);
		transaction.setFromAccount(fromAccount);
		transaction.setToAccount(toAccount);
		transaction.setTransactionAmount(amount);

		try {
			bankAppLogger.debug("Persisting data: " + transaction);

			entityManager.persist(transaction);
			bankAppLogger.info("Data persisted: " + transaction);

			fromAccount.setAccountBalance(fromAccount.getAccountBalance() - amount);
			bankAppLogger.debug("Persisting data: " + fromAccount);
			entityManager.persist(fromAccount);
			bankAppLogger.info("Data persisted: " + fromAccount);

			toAccount.setAccountBalance(toAccount.getAccountBalance() + amount);
			bankAppLogger.debug("Persisting data: " + toAccount);
			entityManager.persist(toAccount);
			bankAppLogger.info("Data persisted: " + toAccount);
		} catch (Exception e) {
			throw new TransactionFailedException(e.getMessage());
		}

		return transaction.getTransactionId();
	}

}
