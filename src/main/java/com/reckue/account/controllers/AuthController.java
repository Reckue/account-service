package com.reckue.account.controllers;

import com.reckue.account.controllers.apis.AuthApi;
import com.reckue.account.services.AuthService;
import com.reckue.account.transfers.AuthTransfer;
import com.reckue.account.transfers.LoginRequest;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.transfers.UserTransfer;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Class AuthСontroller represents a REST-Controller with post and get operations connection with authentication.
 *
 * @author Kamila Meshcheryakova
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController implements AuthApi {

    private final Mapper mapper;
    private final AuthService authService;

    /**
     * This type of request allows to register a new user.
     *
     * @param registerForm with required fields
     * @return the object of class AuthTransfer
     */
    @PostMapping("/register")
    public AuthTransfer register(@RequestBody RegisterRequest registerForm) {
        return authService.register(registerForm);
    }

    /**
     * This type of request allows an authorized user to log in.
     *
     * @param loginForm with required fields
     * @return the object of class AuthTransfer
     */
    @PostMapping("/login")
    public AuthTransfer login(@RequestBody LoginRequest loginForm) {
        return authService.login(loginForm);
    }

    /**
     * This type of request allows to get the user by his token.
     *
     * @param request information for HTTP servlets
     * @return the object of class UserTransfer
     */
    @GetMapping(value = "/currentUser")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public UserTransfer getCurrentUser(HttpServletRequest request) {
        return mapper.map(authService.getCurrentUser(request), UserTransfer.class);
    }

    /**
     * This type of request allows to update the token of an authorized user.
     *
     * @param refreshToken token of an authorized user
     * @param user         authorized user
     * @return the object of class AuthTransfer
     */
    @GetMapping(value = "/refreshToken")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public AuthTransfer refresh(@RequestParam(name = "refreshToken") String refreshToken,
                                @AuthenticationPrincipal User user) {
        return authService.refresh(user.getUsername(), refreshToken);
    }
}
