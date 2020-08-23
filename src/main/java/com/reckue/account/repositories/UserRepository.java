package com.reckue.account.repositories;

import com.reckue.account.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface UserRepository configures the connection with PostgreSQL.
 *
 * @author Kamila Meshcheryakova
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * This method is used to check if the user with that username already exists in the database.
     *
     * @param username name of user
     * @return true or false
     */
    boolean existsByUsername(String username);

    /**
     * This method is used to check if the user with that email already exists in the database.
     *
     * @param email user email
     * @return true or false
     */
    boolean existsByEmail(String email);

    /**
     * This method is used to find the user by username in the database.
     *
     * @param username name of user
     * @return the object of class User with that username
     */
    Optional<User> findByUsername(String username);

    /**
     * This method is used to delete the user by username in the database.
     *
     * @param username name of user
     */
    void deleteByUsername(String username);
}
