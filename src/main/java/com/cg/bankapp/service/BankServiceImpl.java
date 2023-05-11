package com.cg.bankapp.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.bankapp.dto.Account;
import com.cg.bankapp.dto.Customer;
import com.cg.bankapp.dto.Transaction;
import com.cg.bankapp.exception.AccountBalanceException;
import com.cg.bankapp.exception.AccountNotFoundException;
import com.cg.bankapp.exception.InvalidAmountException;
import com.cg.bankapp.exception.InvalidFundTransferException;
import com.cg.bankapp.exception.NoTransactionsFoundException;
import com.cg.bankapp.exception.TransactionFailedException;
import com.cg.bankapp.model.AccountEntity;
import com.cg.bankapp.model.CustomerEntity;
import com.cg.bankapp.model.TransactionEntity;
import com.cg.bankapp.repository.BankRepository;
import com.cg.bankapp.utils.DataMapper;
import com.cg.bankapp.utils.TransactionType;

@Service
public class BankServiceImpl implements BankService {

	@Autowired
	BankRepository bankRepository;

	public BankServiceImpl() {
	}

	protected BankServiceImpl(BankRepository mockBankRepository) {
		bankRepository = mockBankRepository;
	}

	@Override
	public Account createAccount(Customer customer) throws TransactionFailedException {

		CustomerEntity customerEntity = new CustomerEntity();
		customer.setCustomerName(customer.getCustomerName());

		AccountEntity account = new AccountEntity();
		account.setCustomer(customerEntity);

		try {
			account = bankRepository.save(account);
		} catch (RuntimeException e) {
			throw new TransactionFailedException(e.getMessage());
		}

		return DataMapper.convertAccountEntityToDTO(account);
	}

	@Override
	public Double showBalance(Long accountNo) throws AccountNotFoundException {
		AccountEntity accountEntity = bankRepository.findById(accountNo).orElseThrow(AccountNotFoundException::new);
		return accountEntity.getAccountBalance();
	}

	@Override
	public Transaction deposit(Long accountNo, Double amount)
			throws AccountNotFoundException, InvalidAmountException, TransactionFailedException {
		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Deposit amount cannot be negative or zero.");

		AccountEntity account = bankRepository.findById(accountNo).orElseThrow(AccountNotFoundException::new);

		TransactionEntity transaction = new TransactionEntity();
		transaction.setToAccount(account);
		transaction.setTransactionType(TransactionType.CREDIT);
		transaction.setTransactionAmount(amount);

		account.addNewTransaction(transaction);
		account.setAccountBalance(account.getAccountBalance() + amount);

		try {
			bankRepository.save(account);
			transaction = account.getTransactions().get(account.getTransactions().size() - 1);
		} catch (RuntimeException e) {
			throw new TransactionFailedException(e.getMessage());
		}
		return DataMapper.convertTransactionEntityToDTO(transaction);
	}

	@Override
	public Transaction withdraw(Long accountNo, Double amount) throws AccountNotFoundException, InvalidAmountException,
			AccountBalanceException, TransactionFailedException {
		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Withdraw amount cannot be negative or zero.");

		AccountEntity account = bankRepository.findById(accountNo).orElseThrow(AccountNotFoundException::new);

		if (account.getAccountBalance() < amount) {
			throw new AccountBalanceException();
		}

		TransactionEntity transaction = new TransactionEntity();
		transaction.setFromAccount(account);
		transaction.setTransactionType(TransactionType.DEBIT);
		transaction.setTransactionAmount(amount);

		account.addNewTransaction(transaction);
		account.setAccountBalance(account.getAccountBalance() - amount);

		try {
			bankRepository.save(account);
			transaction = account.getTransactions().get(account.getTransactions().size() - 1);
		} catch (RuntimeException e) {
			throw new TransactionFailedException(e.getMessage());
		}

		return DataMapper.convertTransactionEntityToDTO(transaction);
	}

	@Override
	public Transaction fundTransfer(Long fromAccountNo, Long toAccountNo, Double amount)
			throws InvalidFundTransferException, AccountNotFoundException, AccountBalanceException,
			InvalidAmountException, TransactionFailedException {
		if (fromAccountNo == null || toAccountNo == null)
			throw new AccountNotFoundException();

		if (fromAccountNo.equals(toAccountNo))
			throw new InvalidFundTransferException("Cannot transfer funds to the same account number");

		if (amount == null || amount <= 0)
			throw new InvalidAmountException("Transfer amount cannot be negative or zero.");

		AccountEntity fromAccount = bankRepository.findById(fromAccountNo)
				.orElseThrow(() -> new AccountNotFoundException("From account not found."));
		AccountEntity toAccount = bankRepository.findById(toAccountNo)
				.orElseThrow(() -> new AccountNotFoundException("To account not found."));

		if (fromAccount.getAccountBalance() < amount) {
			throw new AccountBalanceException();
		}

		TransactionEntity transaction = new TransactionEntity();
		transaction.setToAccount(toAccount);
		transaction.setFromAccount(fromAccount);
		transaction.setTransactionType(TransactionType.TRANSFER);
		transaction.setTransactionAmount(amount);

		fromAccount.addNewTransaction(transaction);
		fromAccount.setAccountBalance(fromAccount.getAccountBalance() - amount);
		toAccount.setAccountBalance(toAccount.getAccountBalance() + amount);

		try {
			bankRepository.save(fromAccount);
			transaction = fromAccount.getTransactions().get(fromAccount.getTransactions().size() - 1);
		} catch (RuntimeException e) {
			throw new TransactionFailedException(e.getMessage());
		}

		return DataMapper.convertTransactionEntityToDTO(transaction);
	}

	@Override
	public List<Transaction> getAllTransactionDetails(Long accountNo) throws AccountNotFoundException {
		AccountEntity account = bankRepository.findById(accountNo).orElseThrow(AccountNotFoundException::new);

		List<TransactionEntity> transactions = account.getTransactions();

		if (transactions == null || transactions.isEmpty()) {
			throw new NoTransactionsFoundException();
		}

		return transactions.stream().sorted(Comparator.comparing(TransactionEntity::getDatetime).reversed())
				.map(DataMapper::convertTransactionEntityToDTO).limit(10).collect(Collectors.toList());
	}

}
