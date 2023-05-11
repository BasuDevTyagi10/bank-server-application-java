package com.cg.bankapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.bankapp.model.AccountEntity;

/**
 * Repository implementation is enabled by implementing this interface which
 * extends the JpaRepository that allows this interface to save an
 * <code>Account</code> and retrieve any <code>Account</code> by ID and perform
 * any <code>Transaction</code>.
 */
@Repository
public interface BankRepository extends JpaRepository<AccountEntity, Long> {
}
