package com.cg.bankapp.dao;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.exceptions.DataNotFoundException;
import com.cg.bankapp.exceptions.DatabaseLimitException;

/**
 * Bank DataAccesObject (DAO) implementation is enabled by implementing this
 * interface which has methods to save an <code>Account</code> and retrieve any
 * <code>Account</code> by ID from the <code>BankDatabase</code>.
 */
public interface IBankDAO {

	/**
	 * Save an <code>Account</code> in the database.
	 * 
	 * @param account
	 * @return true if the account creation was successful.
	 * @throws DatabaseLimitException
	 */
	public Boolean save(Account account) throws DatabaseLimitException;

	/**
	 * Find <code>Account</code> by accountNo from the database.
	 * 
	 * @param accountNo
	 * @return <code>Account</code> if exists.
	 * @throws DataNotFoundException
	 */
	public Account getAccountById(String accountNo) throws DataNotFoundException;
}
