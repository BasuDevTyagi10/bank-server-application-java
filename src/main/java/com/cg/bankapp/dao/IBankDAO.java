package com.cg.bankapp.dao;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.DatabaseConnectionException;
import com.cg.bankapp.exceptions.DatabaseQueryException;
import com.cg.bankapp.exceptions.InvalidAccountException;

/**
 * Bank DataAccesObject (DAO) implementation is enabled by implementing this
 * interface which has methods to save an <code>Account</code> and retrieve any
 * <code>Account</code> by ID, update balance of an <code>Account</code> and
 * save a <code>Transaction</code> in the <code>BankDatabase</code>.
 */
public interface IBankDAO {

	/**
	 * Save an <code>Account</code> in the database.
	 * 
	 * @param account
	 * @return accountNo of the newly created <code>Account</code> entry in the
	 *         database.
	 * @throws DatabaseConnectionException
	 * @throws DatabaseQueryException
	 * @throws InvalidAccountException
	 */
	public Integer save(Account account)
			throws DatabaseConnectionException, DatabaseQueryException, InvalidAccountException;

	/**
	 * Update the balance of an <code>Account</code> in the database.
	 * 
	 * @param account
	 * @return new balance after update.
	 * @throws DatabaseConnectionException
	 * @throws DatabaseQueryException
	 * @throws InvalidAccountException
	 */
	public Double updateBalance(Account account)
			throws DatabaseConnectionException, DatabaseQueryException, InvalidAccountException;

	/**
	 * Find <code>Account</code> by accountNo from the database.
	 * 
	 * @param accountNo
	 * @return <code>Account</code> if exists.
	 * @throws AccountNotFoundException
	 * @throws DatabaseConnectionException
	 * @throws DatabaseQueryException
	 */
	public Account getAccountById(Integer accountNo)
			throws AccountNotFoundException, DatabaseConnectionException, DatabaseQueryException;

	/**
	 * Create a <code>Transaction</code> in the database.
	 * 
	 * @param transaction
	 * @return transactionId of the newly created <code>Transaction</code> entry in
	 *         the database.
	 * @throws DatabaseConnectionException
	 * @throws DatabaseQueryException
	 */
	public Integer createTransaction(Transaction transaction)
			throws DatabaseConnectionException, DatabaseQueryException;
}
