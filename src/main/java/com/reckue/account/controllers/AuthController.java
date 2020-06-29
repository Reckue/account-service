package com.reckue.account.controllers;

import com.reckue.account.services.AuthService;
import com.reckue.account.transfers.AuthTransfer;
import com.reckue.account.transfers.LoginRequest;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.transfers.UserTransfer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "/auth")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final Mapper mapper;
    private final AuthService authService;

    /**
     * This type of request allows to register a new user.
     *
     * @param registerForm with required fields
     * @return the object of class AuthTransfer
     */
    @ApiOperation(value = "Registration", response = AuthTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Register form has created"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
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
    @ApiOperation(value = "Authorization", response = AuthTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Login form has accepted"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 404, message = "The user by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
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
    @ApiOperation(value = "Get current user", response = UserTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "The request has accepted"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 404, message = "The user by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @GetMapping(value = "/current_user")
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
    @ApiOperation(value = "Updating", response = AuthTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "RefreshToken has updated"),
            @ApiResponse(code = 400, message = "Invalid incoming parameters"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @GetMapping(value = "/refresh_token")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public AuthTransfer refresh(@RequestParam(name = "refresh_token") String refreshToken,
                                @AuthenticationPrincipal User user) {
        return authService.refresh(user.getUsername(), refreshToken);
    }
}
