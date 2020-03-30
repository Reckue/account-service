package com.reckue.authorization.controllers;

import com.reckue.authorization.models.requests.LoginRequest;
import com.reckue.authorization.models.requests.RegisterRequest;
import com.reckue.authorization.models.transfers.AuthTransfer;
import com.reckue.authorization.models.transfers.UserTransfer;
import com.reckue.authorization.services.AuthService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AuthController {

    private final Mapper mapper;
    private final AuthService authService;

    @Autowired
    public AuthController(Mapper mapper, AuthService authService) {
        this.mapper = mapper;
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public AuthTransfer register(@RequestBody RegisterRequest registerForm) {
        return authService.register(registerForm);
    }

    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public AuthTransfer login(@RequestBody LoginRequest loginForm) {
        return authService.login(loginForm);
    }

    @GetMapping(value = "/current_user")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public UserTransfer getCurrentUser(HttpServletRequest request) {
        return mapper.map(authService.getCurrentUser(request), UserTransfer.class);
    }

    @GetMapping(value = "/refresh_token")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public AuthTransfer refresh(@RequestParam(name = "refresh_token") String refreshToken,
                                @AuthenticationPrincipal User user) {
        return authService.refresh(user.getUsername(), refreshToken);
    }
}
