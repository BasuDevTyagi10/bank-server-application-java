package com.cg.bankapp.services;

import java.util.List;

import com.cg.bankapp.beans.Transaction;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.DatabaseException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;

/**
 * Bank Service implementation is enabled by implementing this interface which
 * holds methods related to <code>Account</code> actions like showing balance,
 * deposit, withdraw & transfer funds and show all transactions.
 */
public interface IBankService {
	/**
	 * Shows the balance in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number for which balance needs to be retrieved.
	 * @return Balance in account if account exists else null.
	 * @throws AccountNotFoundException
	 * @throws DatabaseException
	 */
	public Double showBalance(Integer accountNo) throws AccountNotFoundException, DatabaseException;

	/**
	 * Deposit X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number in which the amount will be deposited.
	 * @param amount    The amount which will be deposited.
	 * @return New balance after amount deposit.
	 * @throws InvalidAmountException
	 * @throws DatabaseException
	 * @throws InvalidAccountException
	 */
	public Double deposit(Integer accountNo, Double amount)
			throws AccountNotFoundException, InvalidAmountException, DatabaseException, InvalidAccountException;

	/**
	 * Withdraw X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number in which the amount will be withdrawn.
	 * @param amount    The amount which will be withdrawn.
	 * @return New balance after amount withdraw.
	 * @throws InvalidAmountException
	 * @throws AccountNotFoundException
	 * @throws AccountBalanceException
	 * @throws DatabaseException
	 * @throws InvalidAccountException
	 */
	public Double withdraw(Integer accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, DatabaseException, InvalidAccountException;

	/**
	 * Transfer X amount funds from one <code>Account</code> to another via account
	 * No.
	 * 
	 * @param fromAccountNo   The account number from which the X amount of funds
	 *                        will be transfered.
	 * @param targetAccountNo The account number to which the X amount of funds will
	 *                        be transfered.
	 * @param amount          The amount which will be transfered.
	 * @return true if the transfer was successful else false.
	 * @throws InvalidFundTransferException
	 * @throws AccountNotFoundException
	 * @throws AccountBalanceException
	 * @throws InvalidAmountException
	 * @throws DatabaseException
	 * @throws InvalidAccountException
	 */
	public Boolean fundTransfer(Integer fromAccountNo, Integer targetAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, DatabaseException, InvalidAccountException;

	/**
	 * Get all the <code>Transaction</code> made by an account via accountNo.
	 * 
	 * @param accountNo The account for which the transactions need to be retrieved.
	 * @return An ArrayList of <code>Transaction</code> objects.
	 * @throws AccountNotFoundException
	 * @throws NoTransactionsFoundException
	 * @throws DatabaseException
	 */
	List<Transaction> getAllTransactionDetails(Integer accountNo)
			throws AccountNotFoundException, NoTransactionsFoundException, DatabaseException;
}
