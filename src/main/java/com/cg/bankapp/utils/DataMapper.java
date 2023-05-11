package com.cg.bankapp.utils;

import java.util.ArrayList;
import java.util.List;

import com.cg.bankapp.dto.Account;
import com.cg.bankapp.dto.Customer;
import com.cg.bankapp.dto.Transaction;
import com.cg.bankapp.model.AccountEntity;
import com.cg.bankapp.model.TransactionEntity;

/**
 * This class provides utility methods for mapping data between different
 * representations of objects related to banking transactions.
 */
public class DataMapper {

	/**
	 * Private constructor to prevent instantiation of the class.
	 */
	private DataMapper() {
	}

	/**
	 * Converts an AccountEntity object to an Account DTO object.
	 *
	 * @param accountEntity AccountEntity object to be converted
	 * @return Account DTO object
	 */
	public static Account convertAccountEntityToDTO(AccountEntity accountEntity) {
		Account accountDTO = new Account();
		accountDTO.setAccountNo(accountEntity.getAccountNo());
		accountDTO.setAccountBalance(accountEntity.getAccountBalance());
		accountDTO.setAccountType(accountEntity.getAccountType());

		Customer customerDTO = new Customer();
		customerDTO.setCustomerId(accountEntity.getCustomer().getCustomerId());
		customerDTO.setCustomerName(accountEntity.getCustomer().getCustomerName());

		accountDTO.setCustomer(customerDTO);

		List<Transaction> transactionDTOs = new ArrayList<>();

		List<TransactionEntity> transactionEntities = accountEntity.getTransactions();

		if (!transactionEntities.isEmpty()) {
			for (TransactionEntity transactionEntity : transactionEntities) {
				transactionDTOs.add(convertTransactionEntityToDTO(transactionEntity));
			}
		}

		accountDTO.setTransactions(transactionDTOs);

		return accountDTO;
	}

	/**
	 * Converts a TransactionEntity object to a Transaction DTO object.
	 *
	 * @param transactionEntity TransactionEntity object to be converted
	 * @return Transaction DTO object
	 */
	public static Transaction convertTransactionEntityToDTO(TransactionEntity transactionEntity) {
		Transaction transactionDTO = new Transaction();
		transactionDTO.setTransactionId(transactionEntity.getTransactionId());
		transactionDTO.setTransactionType(transactionEntity.getTransactionType());
		transactionDTO.setDatetime(transactionEntity.getDatetime());

		if (transactionEntity.getFromAccount() != null) {
			transactionDTO.setFromAccount(transactionEntity.getFromAccount().getAccountNo());
		}
		if (transactionEntity.getToAccount() != null) {
			transactionDTO.setToAccount(transactionEntity.getToAccount().getAccountNo());
		}
		transactionDTO.setTransactionAmount(transactionEntity.getTransactionAmount());

		return transactionDTO;
	}

}
