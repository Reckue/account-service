package com.reckue.account.controllers;

import com.reckue.account.controllers.apis.AuthApi;
import com.reckue.account.services.AuthService;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.transfers.UserTransfer;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Class AuthСontroller represents a REST-Controller with post and get operations connection with authentication.
 *
 * @author Kamila Meshcheryakova
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final Mapper mapper;
    private final AuthService authService;
    private final TokenEndpoint tokenEndpoint;

    /**
     * This type of request allows to register a new user.
     *
     * @param registerForm with required fields
     * @return the object of class AuthTransfer             !!!
     */
    @PostMapping("/register")
//    public AuthTransfer register(@RequestBody RegisterRequest registerForm) {
//        return authService.register(registerForm);
//    }
    public String register(@RequestBody RegisterRequest registerForm) {
        authService.register(registerForm);
        return "You have successfully registered";
    }

    /**
     * This type of request allows an authorized user to log in.
     *
     * @param loginForm with required fields
     * @return the object of class AuthTransfer
     */
    /**
     *
     *
     * @param principal
     * @param scope
     * @param grantType
     * @param username
     * @param password
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
//    @PostMapping("/login")
//    public AuthTransfer login(@RequestBody LoginRequest loginForm) {
//        return authService.login(loginForm);
//    }
    @PostMapping("/login")
    public ResponseEntity<OAuth2AccessToken> getToken(Principal principal,
                                                      @ApiParam(example = "write")
                                                      @RequestParam(value = "scope") String scope,
                                                      @ApiParam(example = "password")
                                                      @RequestParam(value = "grant_type") String grantType,
                                                      @RequestParam(name = "refreshToken") String refreshToken,
                                                      @RequestParam(value = "username") String username,
                                                      @RequestParam(value = "password") String password
    ) throws HttpRequestMethodNotSupportedException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("scope", scope);
        parameters.put("grant_type", grantType);
        if (grantType.equals("password")) {
            parameters.put("username", username);
            parameters.put("password", password);
        }
        if (grantType.equals("refresh_token")) {
            parameters.put("refresh_token", refreshToken);
        }
        return authService.login(tokenEndpoint.postAccessToken(principal, parameters));
    }

    /**
     * This type of request allows to get the user by his token.
     *
     * @param request information for HTTP servlets
     * @return the object of class UserTransfer
     */
    @GetMapping(value = "/current")
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
    @GetMapping(value = "/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public OAuth2AccessToken  refresh(@RequestParam(name = "refreshToken") String refreshToken,
                                      HttpServletRequest request) {
        return authService.refresh(refreshToken, request);
    }

    @GetMapping(value = "/login")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<OAuth2AccessToken>  refreshToken(
            Principal principal,
            @ApiParam(example = "write")
            @RequestParam(value = "scope") String scope,
            @RequestParam(name = "refreshToken") String refreshToken,
            @ApiParam(example = "refresh_token")
            @RequestParam(value = "grant_type") String grantType
    ) throws HttpRequestMethodNotSupportedException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", grantType);
        parameters.put("refresh_token", refreshToken);
        parameters.put("scope", scope);
        ResponseEntity<OAuth2AccessToken> responseEntity = tokenEndpoint.postAccessToken(principal, parameters);
        return authService.login(responseEntity);
    }
}
