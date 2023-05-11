package com.cg.bankapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.bankapp.dao.BankDAO;
import com.cg.bankapp.entity.Account;
import com.cg.bankapp.entity.Customer;
import com.cg.bankapp.entity.Transaction;
import com.cg.bankapp.entity.TransactionType;
import com.cg.bankapp.exceptions.AccountBalanceException;
import com.cg.bankapp.exceptions.AccountNotFoundException;
import com.cg.bankapp.exceptions.InvalidAccountException;
import com.cg.bankapp.exceptions.InvalidAmountException;
import com.cg.bankapp.exceptions.InvalidFundTransferException;
import com.cg.bankapp.exceptions.NoTransactionsFoundException;
import com.cg.bankapp.exceptions.TransactionFailedException;

@Service
public class BankServiceImpl implements BankService {

	@Autowired
	BankDAO bankDAO;

	public BankServiceImpl() {
	}

	protected BankServiceImpl(BankDAO testBankDAO) {
		this.bankDAO = testBankDAO;
	}

	@Override
	@Transactional
	public Long createAccount(String customerName) throws InvalidAccountException, TransactionFailedException {
		Customer customer = new Customer();
		customer.setCustomerName(customerName);

		Account account = new Account();
		account.setCustomer(customer);

		return bankDAO.save(account);
	}

	@Override
	public Double showBalance(Long accountNo) throws AccountNotFoundException, TransactionFailedException {
		Account account = bankDAO.getAccountById(accountNo);
		return Optional.ofNullable(account.getAccountBalance()).orElse(0.0);
	}

	@Override
	@Transactional
	public Double deposit(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			InvalidAccountException, TransactionFailedException {
		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Deposit amount cannot be negative or zero.");

		Account account = bankDAO.getAccountById(accountNo);

		bankDAO.performTransaction(account, TransactionType.CREDIT, amount);

		return account.getAccountBalance();
	}

	@Override
	@Transactional
	public Double withdraw(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, InvalidAccountException, TransactionFailedException {
		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Withdraw amount cannot be negative or zero.");

		Account account = bankDAO.getAccountById(accountNo);

		if (account.getAccountBalance() < amount) {
			throw new AccountBalanceException();
		}

		bankDAO.performTransaction(account, TransactionType.DEBIT, amount);

		return account.getAccountBalance();
	}

	@Override
	@Transactional
	public Boolean fundTransfer(Long fromAccountNo, Long targetAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, InvalidAccountException, TransactionFailedException {
		if (fromAccountNo == null || targetAccountNo == null)
			throw new AccountNotFoundException();

		if (fromAccountNo.equals(targetAccountNo))
			throw new InvalidFundTransferException("Cannot transfer funds to the same account number");

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Transfer amount cannot be negative or zero.");

		Account fromAccount = bankDAO.getAccountById(fromAccountNo);
		Account toAccount = bankDAO.getAccountById(targetAccountNo);

		if (fromAccount.getAccountBalance() < amount) {
			throw new AccountBalanceException();
		}

		bankDAO.performTransaction(fromAccount, toAccount, amount);

		return true;
	}

	@Override
	public List<Transaction> getAllTransactionDetails(Long accountNo)
			throws AccountNotFoundException, NoTransactionsFoundException, TransactionFailedException {
		Account account = bankDAO.getAccountById(accountNo);
		List<Transaction> transactions = account.getTransactions();

		if (transactions == null || transactions.isEmpty()) {
			throw new NoTransactionsFoundException();
		}

		return transactions;
	}

}
