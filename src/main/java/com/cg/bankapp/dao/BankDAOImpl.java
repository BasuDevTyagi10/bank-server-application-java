package com.cg.bankapp.dao;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.exceptions.DataNotFoundException;
import com.cg.bankapp.exceptions.DatabaseLimitException;
import com.cg.bankapp.util.BankDatabase;

/**
 * This class implements <code>IBankDAO</code> to interact with
 * <code>BankDatabase</code> by overriding the methods in the parent interface.
 */
public class BankDAOImpl implements IBankDAO {
	Account[] DATABASE = BankDatabase.DATABASE;

	@Override
	public Boolean save(Account account) throws DatabaseLimitException {
		if (account == null) {
			throw new IllegalArgumentException("Account object cannot be null.");
		}
		if (!BankDatabase.isSpaceAvailable()) {
			throw new DatabaseLimitException("Database is full. Delete some values to create space.");
		}
		System.out.println("SAVING " + account);
		DATABASE[BankDatabase.DATABASE_INDEX++] = account;
		return true;
	}

	@Override
	public Account getAccountById(String accountNo) throws DataNotFoundException {
		for (int i = 0; i < BankDatabase.DATABASE_INDEX; i++) {
			if (DATABASE[i] != null) {
				Account account = BankDatabase.DATABASE[i];
				if (account.getAccountNo().equals(accountNo)) {
					return account;
				}
			}
		}
		throw new DataNotFoundException();
	}

}
