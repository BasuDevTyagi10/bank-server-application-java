package com.cg.bankapp.util;

/**
 * This class contains the constant values used for database related operations
 * like name of the persistence unit, queries and their identifiers.
 */
public final class BankDatabaseConstants {
	public static final String FIND_TRANSACTIONS_BY_ACCOUNT_NO_QUERY = "SELECT t FROM Transaction t WHERE :accountNo IN (t.fromAccount.accountNo, t.toAccount.accountNo) ORDER BY t.datetime DESC";
	public static final String FIND_TRANSACTIONS_BY_ACCOUNT_NO_IDENTIFIER = "findTransactionsById";

	private BankDatabaseConstants() {
	}
}
