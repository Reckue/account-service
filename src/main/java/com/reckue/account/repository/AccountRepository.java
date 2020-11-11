package com.reckue.account.repository;

import com.reckue.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface AccountRepository configures the connection with PostgreSQL.
 *
 * @author Kamila Meshcheryakova
 */
@Repository
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface AccountRepository extends JpaRepository<Account, String> {

    /**
     * This method is used to check if the account with that username already exists in the database.
     *
     * @param username name of account
     * @return true or false
     */
    boolean existsByUsername(String username);

    /**
     * This method is used to check if the account with that email already exists in the database.
     *
     * @param email account email
     * @return true or false
     */
    boolean existsByEmail(String email);

    /**
     * This method is used to find the account by username in the database.
     *
     * @param username name of account
     * @return the object of class Account with that username
     */
    Optional<Account> findByUsername(String username);

    /**
     * This method is used to delete the account by username in the database.
     *
     * @param username name of account
     */
    void deleteByUsername(String username);
}
