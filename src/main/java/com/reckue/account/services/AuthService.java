package com.reckue.account.services;

import com.reckue.account.configs.filters.TokenProvider;
import com.reckue.account.exceptions.AuthenticationException;
import com.reckue.account.exceptions.NotFoundException;
import com.reckue.account.models.Role;
import com.reckue.account.models.Status;
import com.reckue.account.models.User;
import com.reckue.account.transfers.LoginRequest;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.transfers.AuthTransfer;
import com.reckue.account.repositories.UserRepository;
import com.reckue.account.utils.helpers.RandomHelper;
import com.reckue.account.utils.helpers.TimestampHelper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthTransfer register(RegisterRequest registerForm) {
        // checking that the user exists in the database
        if (userRepository.existsByUsername(registerForm.getUsername())) {

            // create a new refresh token
            String refreshToken = tokenProvider.createRefreshToken();

            // create instance of user model and fill it
            User user = User.builder()
                    .id(RandomHelper.generate(registerForm.getUsername()))
                    .username(registerForm.getUsername())
                    .email(registerForm.getEmail())
                    .password(passwordEncoder.encode(registerForm.getPassword()))
                    .refreshToken(refreshToken)
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

            // create tokens transfer object and return it
            return AuthTransfer.builder()
                    .accessToken(tokenProvider.createAccessToken(user.getUsername(), user.getRoles()))
                    .refreshToken(refreshToken)
                    .tokenType(tokenProvider.getTokenType())
                    .expiresIn(tokenProvider.getExpire())
                    .build();
        } else {
            throw new AuthenticationException("Username already exists", HttpStatus.BAD_REQUEST);
        }
    }

    public AuthTransfer login(LoginRequest loginForm) {
        try {
            // find user from database
            User user = userRepository.findByUsername(loginForm.getUsername()).orElseThrow(() ->
                    new NotFoundException("The user by username [" + loginForm.getUsername() + "] not found",
                            HttpStatus.NOT_FOUND));

            // create a new refresh token
            String refreshToken = tokenProvider.createRefreshToken();

            // update refresh token
            user.setRefreshToken(refreshToken);

            // update last visit date
            user.setLastVisit(TimestampHelper.getCurrentTimestamp());

            // update user details in database
            userRepository.save(user);

            // authenticate this user in the authentication manager
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(),
                    loginForm.getPassword()));

            // create tokens transfer object and return it
            return AuthTransfer.builder()
                    .accessToken(tokenProvider.createAccessToken(user.getUsername(), user.getRoles()))
                    .refreshToken(refreshToken)
                    .tokenType(tokenProvider.getTokenType())
                    .expiresIn(tokenProvider.getExpire())
                    .build();
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Invalid username or password supplied", HttpStatus.BAD_REQUEST);
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        // get username from jwt token
        String username = tokenProvider.getUsernameByToken(tokenProvider.extractToken(request));

        // find user by username from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("The user by username [" + username + "] not found",
                        HttpStatus.NOT_FOUND));

        // update last visit date
        user.setLastVisit(TimestampHelper.getCurrentTimestamp());

        return user;
    }

    public AuthTransfer refresh(String username, String refreshToken) {
        // get instance of user model by username from database
        Optional<User> userModel = userRepository.findByUsername(username);

        // check for equality of refresh tokens
        if (userModel.isPresent() && userModel.get().getRefreshToken().equals(refreshToken)) {

            // update last visit date
            userModel.get().setLastVisit(TimestampHelper.getCurrentTimestamp());

            return AuthTransfer.builder()
                    .accessToken(tokenProvider.createAccessToken(username, userModel.get().getRoles()))
                    .refreshToken(tokenProvider.createRefreshToken())
                    .tokenType(tokenProvider.getTokenType())
                    .expiresIn(tokenProvider.getExpire())
                    .build();
        } else {
            throw new AuthenticationException("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }
    }
}
