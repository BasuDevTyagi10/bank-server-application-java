package com.cg.bankapp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.entity.TransactionType;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.InvalidAccountException;
import com.cg.bankapp.exception.TransactionFailedException;
import com.cg.bankapp.util.BankDatabase;
import com.cg.bankapp.util.BankDatabaseConstants;
import com.cg.bankapp.util.BankDatabaseEntityManager;
import com.cg.bankapp.util.logging.BankAppLogger;

/**
 * This class implements <code>IBankDAO</code> to interact with
 * <code>BankDatabase</code> by overriding the methods in the parent interface.
 */
public class BankDAOImpl implements IBankDAO {

	private EntityManager entityManager;
	private BankAppLogger bankAppLogger = new BankAppLogger(getClass());

	/**
	 * Constructor to initialize the <code>BankDatabase</code> and set
	 * <code>EntityManager</code> for database interactions.
	 */
	public BankDAOImpl() {
		bankAppLogger.info("Runnning database instance to create accounts...");
		BankDatabase.getInstance();
		bankAppLogger.debug("Fetching instance of entity manager for DML operations.");
		entityManager = BankDatabaseEntityManager.getInstance();
	}

	/**
	 * Constructor to initialize the instance with a test object of EntityManager
	 * for testing purpose.
	 * 
	 * @param testEntityManager A test instance of EntityManager
	 */
	protected BankDAOImpl(EntityManager testEntityManager) {
		bankAppLogger.debug("Creating a test instance of entity manager for DML operations.");
		this.entityManager = testEntityManager;
	}

	@Override
	public Long save(Account account) throws InvalidAccountException, TransactionFailedException {
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

		bankAppLogger.debug("Creating a transaction instance.");
		EntityTransaction entityTransaction = entityManager.getTransaction();

		try {
			bankAppLogger.debug("Beginning transaction...");
			entityTransaction.begin();
			bankAppLogger.debug("Persisting data: " + account);
			entityManager.persist(account);
			bankAppLogger.debug("Commiting transaction...");
			entityTransaction.commit();
		} catch (Exception e) {
			String error = e.getMessage();
			bankAppLogger.error("Transaction Failed: " + error);
			if (entityTransaction.isActive()) {
				bankAppLogger.debug("Rolling back...");
				entityTransaction.rollback();
			}
			throw new TransactionFailedException(error);
		}

		bankAppLogger.info("Data persisted: " + account);
		return account.getAccountNo();
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
		EntityTransaction entityTransaction = entityManager.getTransaction();

		Transaction transaction = new Transaction();

		try {
			bankAppLogger.debug("Beginning transaction...");
			entityTransaction.begin();

			if (transactionType == TransactionType.CREDIT) {
				transaction.setTransactionType(transactionType);
				transaction.setToAccount(account);
				transaction.setTransactionAmount(amount);
				bankAppLogger.debug("Persisting data: " + transaction);
				entityManager.persist(transaction);

				account.setAccountBalance(account.getAccountBalance() + amount);
				bankAppLogger.debug("Persisting data: " + account);
				entityManager.persist(account);
			} else if (transactionType == TransactionType.DEBIT) {
				transaction.setTransactionType(transactionType);
				transaction.setFromAccount(account);
				transaction.setTransactionAmount(amount);
				bankAppLogger.debug("Persisting data: " + transaction);
				entityManager.persist(transaction);

				account.setAccountBalance(account.getAccountBalance() - amount);
				bankAppLogger.debug("Persisting data: " + account);
				entityManager.persist(account);
			}

			bankAppLogger.debug("Commiting transaction...");
			entityTransaction.commit();
		} catch (Exception e) {
			String error = e.getMessage();
			bankAppLogger.error("Transaction Failed: " + error);
			if (entityTransaction.isActive()) {
				bankAppLogger.debug("Rolling back...");
				entityTransaction.rollback();
			}
			throw new TransactionFailedException(error);
		}

		bankAppLogger.info("Data persisted: " + transaction);
		bankAppLogger.info("Data persisted: " + account);
		return transaction.getTransactionId();
	}

	@Override
	public Long performTransaction(Account fromAccount, Account toAccount, Double amount)
			throws TransactionFailedException {
		bankAppLogger.info("Performing bank transction [" + TransactionType.TRANSFER + "] on account: " + fromAccount
				+ " and " + toAccount);
		EntityTransaction entityTransaction = entityManager.getTransaction();

		Transaction transaction = new Transaction();

		try {
			bankAppLogger.debug("Beginning transaction...");
			entityTransaction.begin();

			transaction.setTransactionType(TransactionType.TRANSFER);
			transaction.setFromAccount(fromAccount);
			transaction.setToAccount(toAccount);
			transaction.setTransactionAmount(amount);
			bankAppLogger.debug("Persisting data: " + transaction);
			entityManager.persist(transaction);

			fromAccount.setAccountBalance(fromAccount.getAccountBalance() - amount);
			toAccount.setAccountBalance(toAccount.getAccountBalance() + amount);
			bankAppLogger.debug("Persisting data: " + fromAccount);
			entityManager.persist(fromAccount);
			bankAppLogger.debug("Persisting data: " + toAccount);
			entityManager.persist(toAccount);

			entityTransaction.commit();
		} catch (Exception e) {
			String error = e.getMessage();
			bankAppLogger.error("Transaction Failed: " + error);
			if (entityTransaction.isActive()) {
				bankAppLogger.debug("Rolling back...");
				entityTransaction.rollback();
			}
			throw new TransactionFailedException(error);
		}

		bankAppLogger.info("Data persisted: " + transaction);
		bankAppLogger.info("Data persisted: " + fromAccount);
		bankAppLogger.info("Data persisted: " + toAccount);
		return transaction.getTransactionId();
	}

}
