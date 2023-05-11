package com.cg.bankapp.dao;

import java.util.Map;

import com.cg.bankapp.beans.Account;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.util.BankDatabase;

/**
 * This class implements <code>IBankDAO</code> to interact with
 * <code>BankDatabase</code> by overriding the methods in the parent interface.
 */
public class BankDAOImpl implements IBankDAO {
	Map<String, Account> bankDatabase = BankDatabase.getBankDatabase();

	@Override
	public Boolean save(Account account) throws InvalidAccountException {
		if (account == null) {
			throw new InvalidAccountException("Cannot save a null Account.");
		} else if (account.getCustomer() == null) {
			throw new InvalidAccountException("Cannot save an Account without a Customer.");
		}
		bankDatabase.put(account.getAccountNo(), account);
		return true;
	}

	@Override
	public Account getAccountById(String accountNo) throws AccountNotFoundException {
		Account account = bankDatabase.get(accountNo);
		if (account == null) {
			throw new AccountNotFoundException();
		} else {
			return account;
		}
	}

}
