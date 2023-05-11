package com.cg.bankapp.service;

import java.util.List;

import com.cg.bankapp.dto.Account;
import com.cg.bankapp.dto.Customer;
import com.cg.bankapp.dto.Transaction;
import com.cg.bankapp.exception.AccountBalanceException;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.InvalidAmountException;
import com.cg.bankapp.exception.InvalidFundTransferException;
import com.cg.bankapp.exception.TransactionFailedException;

/**
 * Bank Service implementation is enabled by implementing this interface which
 * holds methods related to <code>Account</code> actions like showing balance,
 * deposit, withdraw & transfer funds and show last 10 transactions.
 */
public interface BankService {

	/**
	 * Creates a new <code>Account</code> in the database.
	 * 
	 * @param customerName The name of the customer
	 * @return The account number generated after saving the account
	 * @throws TransactionFailedException
	 */
	public Account createAccount(Customer customer) throws TransactionFailedException;

	/**
	 * Shows the balance in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number for which balance needs to be retrieved
	 * @return The account's balance if account exists else throws
	 *         AccountNotFoundException
	 * @throws AccountNotFoundException
	 */
	public Double showBalance(Long accountNo) throws AccountNotFoundException;

	/**
	 * Deposit X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number in which the amount will be deposited
	 * @param amount    The amount which will be deposited
	 * @return The <code>Transaction</code> object generated for this transaction
	 * @throws AccountNotFoundException
	 * @throws InvalidAmountException
	 * @throws TransactionFailedException
	 */
	public Transaction deposit(Long accountNo, Double amount)
			throws AccountNotFoundException, InvalidAmountException, TransactionFailedException;

	/**
	 * Withdraw X amount in an <code>Account</code> via accountNo.
	 * 
	 * @param accountNo The account number from which the amount will be withdrawn
	 * @param amount    The amount which will be withdrawn
	 * @return The <code>Transaction</code> object generated for this transaction
	 * @throws InvalidAmountException
	 * @throws AccountNotFoundException
	 * @throws AccountBalanceException
	 * @throws TransactionFailedException
	 */
	public Transaction withdraw(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, TransactionFailedException;

	/**
	 * Transfer X amount funds from one <code>Account</code> to another via account
	 * number.
	 * 
	 * @param fromAccountNo The account number from which the X amount of funds will
	 *                      be transfered
	 * @param toAccountNo   The account number to which the X amount of funds will
	 *                      be transfered
	 * @param amount        The amount which will be transfered
	 * @return The <code>Transaction</code> object generated for this transaction
	 * @throws InvalidFundTransferException
	 * @throws AccountNotFoundException
	 * @throws AccountBalanceException
	 * @throws InvalidAmountException
	 * @throws TransactionFailedException
	 */
	public Transaction fundTransfer(Long fromAccountNo, Long toAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, TransactionFailedException;

	/**
	 * Get last 10 <code>Transaction</code> made by an account via accountNo.
	 * 
	 * @param accountNo The account for which the transactions need to be retrieved
	 * @return A List of <code>Transaction</code> objects
	 * @throws AccountNotFoundException
	 */
	List<Transaction> getAllTransactionDetails(Long accountNo) throws AccountNotFoundException;
}
