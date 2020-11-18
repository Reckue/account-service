package com.reckue.account.service;

import com.reckue.account.exception.AuthenticationException;
import com.reckue.account.exception.InvalidDataException;
import com.reckue.account.exception.NotFoundException;
import com.reckue.account.model.Account;
import com.reckue.account.model.Role;
import com.reckue.account.model.Status;
import com.reckue.account.repository.AccountRepository;
import com.reckue.account.transfer.RegisterRequest;
import com.reckue.account.util.helper.RandomHelper;
import com.reckue.account.util.helper.TimestampHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;

/**
 * Class AuthService represents service with operations related to authentication and authorization.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenStore tokenStore;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * This method is used to register a new account.
     * Throws {@link AuthenticationException} in case if user with such username already exists.
     *
     * @param registerForm with required fields
     */
    @Transactional
    public void register(RegisterRequest registerForm) {
        // todo: send a mail about registration to user email
        //checking that the account exists in the database
        if (!accountRepository.existsByUsername(registerForm.getUsername())) {

            // check password verification
            if (!registerForm.getPassword().matches("(.*).{6,}")) {
                throw new InvalidDataException("Your password must contain at least 6 any symbols.",
                        HttpStatus.BAD_REQUEST);
            }
            // check email verification
            if (!registerForm.getEmail().matches("\\b[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}\\b")) {
                throw new InvalidDataException("Please correct, it isn't an email. Use lowercase letters.",
                        HttpStatus.BAD_REQUEST);
            }

            // create instance of account model and fill it
            Account account = Account.builder()
                    .id(RandomHelper.generate(registerForm.getUsername()))
                    .username(registerForm.getUsername())
                    .email(registerForm.getEmail())
                    .password(passwordEncoder.encode(registerForm.getPassword()))
                    .roles(new HashSet<>())
                    .status(Status.ACTIVE)
                    .refreshToken("none")
                    .created(TimestampHelper.getCurrentTimestamp())
                    .updated(TimestampHelper.getCurrentTimestamp())
                    .lastVisit(TimestampHelper.getCurrentTimestamp())
                    .build();

            // add roles to instance of account model
            account.getRoles().add(new Role("ROLE_USER"));

            // save the account in database
            accountRepository.save(account);

        } else {
            throw new AuthenticationException("Username already exists", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method is used to save refresh token of the account in database
     * and in case of grandType "refresh_token" is used to check the validity of the refresh token.
     * Throws {@link NotFoundException} in case if such account isn't contained in database.
     * Throws {@link AuthenticationException} in case if user enters invalid refresh token.
     *
     * @param responseEntity JWT
     * @param refreshToken   "" or saved refresh token of the user
     */
    @Transactional
    public void saveAndCheckRefreshToken(ResponseEntity<OAuth2AccessToken> responseEntity, String refreshToken) {
        String userId = (String) Objects.requireNonNull(responseEntity.getBody()).getAdditionalInformation().get("userId");
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The account by id [" + userId + "] not found",
                        HttpStatus.NOT_FOUND));
        if (!refreshToken.isEmpty() && !account.getRefreshToken().equals(refreshToken)) {
            throw new AuthenticationException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        // update last visit date
        account.setLastVisit(TimestampHelper.getCurrentTimestamp());
        account.setRefreshToken(responseEntity.getBody().getRefreshToken().toString());
    }

    /**
     * This method is used to get the account by user token.
     * Throws {@link NotFoundException} in case if such account isn't contained in database.
     * Throws {@link AuthenticationException} in case of invalid token.
     *
     * @param token user token
     * @return the object of class AccountTransfer
     */
    public Account getCurrentUser(String token) {
        // get userId from jwt token
        try {
            String userId = (String) tokenStore.readAccessToken(token).getAdditionalInformation().get("userId");

            // find account by username from database
            Account account = accountRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("The account by id [" + userId + "] not found",
                            HttpStatus.NOT_FOUND));

            // update last visit date
            account.setLastVisit(TimestampHelper.getCurrentTimestamp());

            return account;
        } catch (Exception e) {
            throw new AuthenticationException("Invalid token", HttpStatus.UNAUTHORIZED);
        }

    }
}
