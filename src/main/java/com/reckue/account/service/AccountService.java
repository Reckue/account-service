package com.reckue.account.service;

import com.reckue.account.exception.AccessDeniedException;
import com.reckue.account.exception.AlreadyExistsException;
import com.reckue.account.exception.NotFoundException;
import com.reckue.account.model.Account;
import com.reckue.account.model.Role;
import com.reckue.account.model.Status;
import com.reckue.account.repository.AccountRepository;
import com.reckue.account.util.helper.RandomHelper;
import com.reckue.account.util.helper.TimestampHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class AccountService represents service with operations related to the account and the database.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * This method is used to find all the users in the database that meet the requirements.
     *
     * @param limit  quantity of objects
     * @param offset quantity to skip
     * @param sort   parameter for sorting
     * @param desc   sorting descending or ascending
     * @return list of given quantity of objects of class Account with a given offset
     * sorted by the selected parameter for sorting in descending or ascending order
     */
    public List<Account> findAll(int limit, int offset, String sort, boolean desc) {
        Sort sorted = desc ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        return accountRepository.findAll(PageRequest.of(offset, limit, sorted)).getContent();
    }

    /**
     * This method is used to find the account by id in the database.
     * Throws {@link NotFoundException} in case if such account isn't contained in database.
     *
     * @param id the object identifier
     * @return the object of class Account
     */
    public Account findById(String id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new NotFoundException("The account by id '" + id + "' not found", HttpStatus.NOT_FOUND));
    }

    /**
     * This method is used to create an object of class Account.
     * Throws {@link AlreadyExistsException} in case if account with such username or email already exists.
     *
     * @param accountModel object of class Account
     * @return the object of class Account
     */
    public Account create(Account accountModel) {
        if (!accountRepository.existsByUsername(accountModel.getUsername())
                && !accountRepository.existsByEmail(accountModel.getEmail())) {
            Account account = Account.builder()
                    .id(RandomHelper.generate(accountModel.getUsername()))
                    .username(accountModel.getUsername())
                    .email(accountModel.getEmail())
                    .password(passwordEncoder.encode(accountModel.getPassword()))
                    .roles(new HashSet<>())
                    .status(Status.ACTIVE)
                    .created(TimestampHelper.getCurrentTimestamp())
                    .updated(TimestampHelper.getCurrentTimestamp())
                    .build();
            account.getRoles().add(new Role("ROLE_USER"));
            return accountRepository.save(account);
        } else {
            throw new AlreadyExistsException("Username or Email already exists", HttpStatus.NOT_MODIFIED);
        }
    }

    /**
     * This method is used to delete the account by username.
     * Throws {@link NotFoundException} in case if such account isn't contained in database.
     * Throws {@link AccessDeniedException} in case if the user isn't the same user or
     * hasn't admin authorities.
     *
     * @param username  the object name
     * @param tokenInfo additional information from a token
     */
    public void deleteByUsername(String username, Map<String, Object> tokenInfo) {
        if (!accountRepository.existsByUsername(username)) {
            throw new NotFoundException("The account by username '" + username + "' not found", HttpStatus.NOT_FOUND);
        }
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isPresent()) {
            String userId = account.get().getId();
            if (tokenInfo.get("userId").equals(userId) || tokenInfo.get("authorities").equals("ROLE_ADMIN")) {
                accountRepository.deleteByUsername(username);
            } else {
                throw new AccessDeniedException("The operation forbidden", HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * This method is used to delete the account by id.
     * Throws {@link NotFoundException} in case if such account isn't contained in database.
     * Throws {@link AccessDeniedException} in case if the user isn't the same user or
     * hasn't admin authorities.
     *
     * @param id        the object identifier
     * @param tokenInfo additional information from a token
     */
    public void deleteById(String id, Map<String, Object> tokenInfo) {
        if (!accountRepository.existsById(id)) {
            throw new NotFoundException("The account by id '" + id + "' not found", HttpStatus.NOT_FOUND);
        }
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            String userId = account.get().getId();
            if (tokenInfo.get("userId").equals(userId) || tokenInfo.get("authorities").equals("ROLE_ADMIN")) {
                accountRepository.deleteById(id);
            } else {
                throw new AccessDeniedException("The operation forbidden", HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * This method is used to find the account by username in the database.
     * Throws {@link NotFoundException} in case if such account isn't contained in database.
     *
     * @param username the object name
     * @return the object of class User
     */
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("The account by username '" + username + "' not found",
                        HttpStatus.NOT_FOUND));
    }
}
