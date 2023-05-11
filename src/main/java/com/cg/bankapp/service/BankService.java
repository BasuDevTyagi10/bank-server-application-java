package com.cg.bankapp.service;

import java.util.List;

import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.exceptions.TransactionFailedException;

/**
 * Bank Service implementation is enabled by implementing this interface which
 * holds methods related to <code>Account</code> actions like showing balance,
 * deposit, withdraw & transfer funds and show last 10 transactions.
 */
public interface BankService {

	/**
	 * Creates a new <code>Account</code> in the database.
	 * 
	 * @param customerName The name of the customer.
	 * @return Account Number generated after saving the account.
	 * @throws InvalidAccountException
	 * @throws TransactionFailedException
	 */
	public Long createAccount(String customerName) throws InvalidAccountException, TransactionFailedException;

	/**
	 * Shows the balance in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number for which balance needs to be retrieved.
	 * @return Balance in account if account exists else null.
	 * @throws AccountNotFoundException
	 * @throws TransactionFailedException
	 */
	public Double showBalance(Long accountNo) throws AccountNotFoundException, TransactionFailedException;

	/**
	 * Deposit X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number in which the amount will be deposited.
	 * @param amount    The amount which will be deposited.
	 * @return New balance after amount deposit.
	 * @throws AccountNotFoundException
	 * @throws InvalidAmountException
	 * @throws InvalidAccountException
	 * @throws TransactionFailedException
	 */
	public Double deposit(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			InvalidAccountException, TransactionFailedException;

	/**
	 * Withdraw X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number in which the amount will be withdrawn.
	 * @param amount    The amount which will be withdrawn.
	 * @return New balance after amount withdraw.
	 * @throws InvalidAmountException
	 * @throws AccountNotFoundException
	 * @throws AccountBalanceException
	 * @throws InvalidAccountException
	 * @throws TransactionFailedException
	 */
	public Double withdraw(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, InvalidAccountException, TransactionFailedException;

	/**
	 * Transfer X amount funds from one <code>Account</code> to another via account
	 * number.
	 * 
	 * @param fromAccountNo   The account number from which the X amount of funds
	 *                        will be transfered.
	 * @param targetAccountNo The account number to which the X amount of funds will
	 *                        be transfered.
	 * @param amount          The amount which will be transfered.
	 * @return true if the transfer was successful.
	 * @throws InvalidFundTransferException
	 * @throws AccountNotFoundException
	 * @throws AccountBalanceException
	 * @throws InvalidAmountException
	 * @throws InvalidAccountException
	 * @throws TransactionFailedException
	 */
	public Boolean fundTransfer(Long fromAccountNo, Long targetAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, InvalidAccountException, TransactionFailedException;

	/**
	 * Get last 10 <code>Transaction</code> made by an account via accountNo.
	 * 
	 * @param accountNo The account for which the transactions need to be retrieved.
	 * @return An ArrayList of <code>Transaction</code> objects.
	 * @throws AccountNotFoundException
	 * @throws NoTransactionsFoundException
	 * @throws TransactionFailedException
	 */
	List<Transaction> getAllTransactionDetails(Long accountNo)
			throws AccountNotFoundException, NoTransactionsFoundException, TransactionFailedException;
}
