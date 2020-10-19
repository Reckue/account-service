package com.reckue.account.controllers;

import com.reckue.account.controllers.apis.AuthApi;
import com.reckue.account.exceptions.AuthenticationException;
import com.reckue.account.services.AuthService;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.transfers.UserTransfer;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.HttpStatus;
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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
    private final TokenEndpoint tokenEndpoint;

    /**
     * This type of request allows to register a new user.
     *
     * @param registerForm with required fields
     * @return string
     */
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerForm) {
        authService.register(registerForm);
        return "You have successfully registered";
    }

    /**
     * This type of request allows an authorized user to log in using such params as:
     * scope, grantType - password, username and password, -
     * or allows to refresh the existing token using such params as:
     * scope, grantType - refresh_token, refreshToken.
     *
     * @param principal    - client
     * @param scope        - allowed scope like "write"
     * @param grantType    - "password" or "refresh_token"
     * @param username     - name of the user - not necessary for grantType "refresh_token"
     * @param password     - password of the user - not necessary for grantType "refresh_token"
     * @param refreshToken - saved refreshToken of the user - not necessary for grantType "password"
     * @return JWT
     */
    @PostMapping("/token")
    public ResponseEntity<OAuth2AccessToken> getToken(Principal principal,
                                                      @ApiParam(example = "write")
                                                      @RequestParam(value = "scope") String scope,
                                                      @ApiParam(example = "password")
                                                      @RequestParam(value = "grant_type") String grantType,
                                                      @RequestParam(value = "username", required = false) String username,
                                                      @RequestParam(value = "password", required = false) String password,
                                                      @RequestParam(name = "refreshToken", defaultValue = "") String refreshToken
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
        ResponseEntity<OAuth2AccessToken> jwt = tokenEndpoint.postAccessToken(principal, parameters);
        authService.saveAndCheckRefreshToken(jwt, refreshToken);
        return jwt;
    }

    /**
     * This type of request allows to get the user by his token.
     * Throws {@link AuthenticationException} in the absence of a token.
     *
     * @param request information for HTTP servlets
     * @return the object of class UserTransfer
     */
    @GetMapping(value = "/current")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public UserTransfer getCurrentUser(HttpServletRequest request) {
        try {
            String token = request.getHeader(AUTHORIZATION).substring(7);
            return mapper.map(authService.getCurrentUser(token), UserTransfer.class);
        } catch (NullPointerException e) {
            throw new AuthenticationException("Token missing", HttpStatus.BAD_REQUEST);
        } catch (StringIndexOutOfBoundsException e) {
            throw new AuthenticationException("Token is too short", HttpStatus.BAD_REQUEST);
        }
    }
}
