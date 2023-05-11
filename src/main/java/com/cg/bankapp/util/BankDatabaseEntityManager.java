package com.cg.bankapp.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * This class contains the JPA <code>EntityManager</code> created using
 * <code>EntityManagerFactory</code> for the persistence unit specified in the
 * persistence.xml file.
 */
public class BankDatabaseEntityManager {
	private static final EntityManagerFactory emf = Persistence
			.createEntityManagerFactory(BankDatabaseConstants.BANK_DATABASE_PU);
	/**
	 * Entity manager for interacting with the BankServerApplication database.
	 */
	private static EntityManager entityManager = null;

	private BankDatabaseEntityManager() {
	}

	public static EntityManager getInstance() {
		if (entityManager == null) {
			entityManager = emf.createEntityManager();
		}
		return entityManager;
	}

}
