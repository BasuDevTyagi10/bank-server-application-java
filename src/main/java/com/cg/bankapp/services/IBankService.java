package com.cg.bankapp.services;

import com.cg.bankapp.beans.Transaction;

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
	 */
	public Double showBalance(String accountNo);

	/**
	 * Deposit X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number in which the amount will be deposited.
	 * @param amount    The amount which will be deposited.
	 * @return New balance after amount deposit.
	 */
	public Double deposit(String accountNo, Double amount);

	/**
	 * Withdraw X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number in which the amount will be withdrawn.
	 * @param amount    The amount which will be withdrawn.
	 * @return New balance after amount withdraw.
	 */
	public Double withdraw(String accountNo, Double amount);

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
	 */
	public Boolean fundTransfer(String fromAccountNo, String targetAccountNo, Double amount);

	/**
	 * Get all the <code>Transaction</code> made by an account via accountNo.
	 * 
	 * @param accountNo The account for which the transactions need to be retrieved.
	 * @return An array of <code>Transaction</code> objects.
	 */
	Transaction[] getAllTransactionDetails(String accountNo);
}
