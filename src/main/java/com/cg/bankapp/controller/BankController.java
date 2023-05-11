package com.cg.bankapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cg.bankapp.dto.Account;
import com.cg.bankapp.dto.Customer;
import com.cg.bankapp.dto.InterAccountTransactionRequest;
import com.cg.bankapp.dto.SingleAccountTransactionRequest;
import com.cg.bankapp.dto.Transaction;
import com.cg.bankapp.exception.BankServerException;
import com.cg.bankapp.service.BankService;

/**
 * 
 * This class represents the REST controller for the Bank Server Application. It
 * handles HTTP requests and responses for various functionalities like creating
 * an account, getting the account balance, doing transactions like deposit,
 * withdraw and fund transfer. It uses the BankService to perform the required
 * operations on the accounts and transactions.
 */
@RestController
@RequestMapping("/api/v1")
public class BankController {

	@Autowired
	BankService bankService;

	/**
	 * Default constructor for the BankController class.
	 */
	public BankController() {
	}

	/**
	 * Constructor for the BankController class which allows the use of a mock
	 * BankService.
	 * 
	 * @param mockBankService mock service for testing purposes
	 */
	protected BankController(BankService mockBankService) {
		bankService = mockBankService;
	}

	/**
	 * Handles a GET request to the root endpoint ("/") and returns a message
	 * indicating that the Bank Server REST API is running.
	 * 
	 * @return the response entity containing the message and the HTTP status code
	 */
	@GetMapping(value = "/")
	public ResponseEntity<String> home() {
		return new ResponseEntity<>("Bank Server REST API is running.", HttpStatus.OK);
	}

	/**
	 * Handles a POST request to create a new account for the given customer.
	 * 
	 * @param customer the customer object to create an account for
	 * @return the response entity containing the created account and the HTTP
	 *         status code
	 * @throws BankServerException
	 */
	@PostMapping(value = "/account")
	public ResponseEntity<Account> handleCreateAccount(@RequestBody Customer customer) throws BankServerException {
		Account account = bankService.createAccount(customer);
		return new ResponseEntity<>(account, HttpStatus.CREATED);
	}

	/**
	 * Handles a GET request to get the balance of the account with the given
	 * account number.
	 * 
	 * @param accountNo the account number of the account to get the balance for
	 * @return the response entity containing the account balance and the HTTP
	 *         status code
	 * @throws BankServerException
	 */
	@GetMapping(value = "/account/{accountNo}/balance")
	public ResponseEntity<String> handleShowBalance(@PathVariable Long accountNo) throws BankServerException {
		String result = "Account balance for Account No. " + accountNo + ": Rs. " + bankService.showBalance(accountNo);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Handles the POST request to create a deposit transaction on the account and
	 * update the balance of the account.
	 * 
	 * @param accountNo          the account number of the account to deposit amount
	 *                           to
	 * @param depositTransaction transaction request object with the amount to
	 *                           deposit
	 * @return response entity with the transaction details and the HTTP status code
	 * @throws BankServerException
	 */
	@PostMapping(value = "/account/{accountNo}/transaction/deposit")
	public ResponseEntity<Transaction> handleDeposit(@PathVariable Long accountNo,
			@RequestBody SingleAccountTransactionRequest depositTransaction) throws BankServerException {
		Double amount = depositTransaction.getAmount();
		return new ResponseEntity<>(bankService.deposit(accountNo, amount), HttpStatus.CREATED);
	}

	/**
	 * Handles the POST request to create a withdraw transaction on the account and
	 * update the balance of the account.
	 * 
	 * @param accountNo           the account number of the account from which to
	 *                            withdraw amount
	 * @param withdrawTransaction transaction request object with the amount to
	 *                            deposit
	 * @return response entity with the transaction details and the HTTP status code
	 * @throws BankServerException
	 */
	@PostMapping(value = "/account/{accountNo}/transaction/withdraw")
	public ResponseEntity<Transaction> handleWithdraw(@PathVariable Long accountNo,
			@RequestBody SingleAccountTransactionRequest witdrawTransaction) throws BankServerException {
		Double amount = witdrawTransaction.getAmount();
		return new ResponseEntity<>(bankService.withdraw(accountNo, amount), HttpStatus.CREATED);
	}

	/**
	 * Handles the POST request to create a fundTransfer transaction from one
	 * account to another and update the balance of the interacting accounts.
	 * 
	 * @param accountNo               the account number of the account from which
	 *                                to withdraw amount and transfer to destination
	 *                                account
	 * @param fundTransferTransaction transaction request object with the
	 *                                destination account and the amount to be
	 *                                transfered
	 * @return response entity with the transaction details and the HTTP status code
	 * @throws BankServerException
	 */
	@PostMapping(value = "/account/{accountNo}/transaction/fund-transfer")
	public ResponseEntity<Transaction> handleFundTransfer(@PathVariable Long accountNo,
			@RequestBody InterAccountTransactionRequest fundTransferTransaction) throws BankServerException {
		Long toAccountNo = fundTransferTransaction.getToAccountNo();
		Double amount = fundTransferTransaction.getAmount();
		return new ResponseEntity<>(bankService.fundTransfer(accountNo, toAccountNo, amount), HttpStatus.CREATED);
	}

	/**
	 * Handles the GET request to get the last 10 transactions of the account from
	 * latest to oldest for the given account number.
	 * 
	 * @param accountNo the account number of the account to get the transactions
	 *                  for
	 * @return the response entity containing the account transactions and the HTTP
	 *         status code
	 * @throws BankServerException
	 */
	@GetMapping(value = "/account/{accountNo}/transactions")
	public ResponseEntity<List<Transaction>> handleGetLast10Transactions(@PathVariable Long accountNo)
			throws BankServerException {
		return new ResponseEntity<>(bankService.getAllTransactionDetails(accountNo), HttpStatus.OK);
	}

}
