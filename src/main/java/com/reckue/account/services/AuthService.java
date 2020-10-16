package com.reckue.account.services;

import com.reckue.account.exceptions.AuthenticationException;
import com.reckue.account.exceptions.InvalidDataException;
import com.reckue.account.exceptions.NotFoundException;
import com.reckue.account.models.Role;
import com.reckue.account.models.Status;
import com.reckue.account.models.User;
import com.reckue.account.repositories.UserRepository;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.utils.helpers.RandomHelper;
import com.reckue.account.utils.helpers.TimestampHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Class AuthService represents service with operations related to authentication and authorization.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final TokenStore tokenStore;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * This method is used to register a new user.
     * Throws {@link AuthenticationException} in case if user with such username already exists.
     *
     * @param registerForm with required fields
     */
    public void register(RegisterRequest registerForm) {
        // checking that the user exists in the database
        if (!userRepository.existsByUsername(registerForm.getUsername())) {

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

            // create instance of user model and fill it
            User user = User.builder()
                    .id(RandomHelper.generate(registerForm.getUsername()))
                    .username(registerForm.getUsername())
                    .email(registerForm.getEmail())
                    .password(passwordEncoder.encode(registerForm.getPassword()))
                    .roles(new HashSet<>())
                    .status(Status.ACTIVE)
                    .created(TimestampHelper.getCurrentTimestamp())
                    .updated(TimestampHelper.getCurrentTimestamp())
                    .lastVisit(TimestampHelper.getCurrentTimestamp())
                    .build();

            // add roles to instance of user model
            user.getRoles().add(new Role("ROLE_USER"));

            // save the user in database
            userRepository.save(user);

        } else {
            throw new AuthenticationException("Username already exists", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method is used to save refresh token of the user in database
     * and in case of grandType "refresh_token" is used to check the validity of the refresh token.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     * Throws {@link AuthenticationException} in case if user enters invalid refresh token.
     *
     * @param responseEntity JWT
     * @param refreshToken "" or saved refresh token of the user
     */
    public void saveAndCheckRefreshToken (ResponseEntity<OAuth2AccessToken> responseEntity, String refreshToken) {
        String userId = (String) Objects.requireNonNull(responseEntity.getBody()).getAdditionalInformation().get("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user by userId [" + userId + "] not found",
                        HttpStatus.NOT_FOUND));
        if (!refreshToken.isEmpty() && !user.getRefreshToken().equals(refreshToken)) {
            throw new AuthenticationException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        // update last visit date
        user.setLastVisit(TimestampHelper.getCurrentTimestamp());
        user.setRefreshToken(responseEntity.getBody().getRefreshToken().toString());
    }
    /**
     * This method is used to get the user by his token.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     * Throws {@link AuthenticationException} in the absence of a token.
     *
     * @param request information for HTTP servlets
     * @return the object of class UserTransfer
     */
    public User getCurrentUser(HttpServletRequest request) {
        // get username from jwt token
        String token = request.getHeader(AUTHORIZATION).substring(7);
        log.info("token = " + token);
        if (token.length() < 15) {
            throw new AuthenticationException("There isn't any token", HttpStatus.UNAUTHORIZED);
        }
        String userId = (String) tokenStore.readAccessToken(token).getAdditionalInformation().get("userId");

        // find user by username from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user by userId [" + userId + "] not found",
                        HttpStatus.NOT_FOUND));

        // update last visit date
        user.setLastVisit(TimestampHelper.getCurrentTimestamp());

        return user;
    }
}
