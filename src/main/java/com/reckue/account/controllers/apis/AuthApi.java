package com.reckue.account.controllers.apis;

import com.reckue.account.transfers.AuthTransfer;
import com.reckue.account.transfers.LoginRequest;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.transfers.UserTransfer;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Interface AuthApi allows to post annotations for swagger.
 *
 * @author Kamila Meshcheryakova
 */
@Api(tags = "/auth")
@SuppressWarnings("unused")
public interface AuthApi {

    @ApiOperation(value = "Registration")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Register form has created"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
//    AuthTransfer register(RegisterRequest registerForm);
    String register(RegisterRequest registerForm);

    @ApiOperation(value = "Authorization", response = OAuth2AccessToken.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Login form has accepted"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "The user by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
   // AuthTransfer login(LoginRequest loginForm);
    ResponseEntity<OAuth2AccessToken> getToken(Principal principal, String scope, String grantType, String username,
                                               String password) throws HttpRequestMethodNotSupportedException;


    @ApiOperation(value = "Get current user", response = UserTransfer.class,
            authorizations = {@Authorization(value = "Bearer token")})
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "The request has accepted"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 404, message = "The user by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    UserTransfer getCurrentUser(HttpServletRequest request);

    @ApiOperation(value = "Updating", response = OAuth2AccessToken.class,
            authorizations = {@Authorization(value = "Bearer token")})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "RefreshToken has updated"),
            @ApiResponse(code = 400, message = "Invalid incoming parameters"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    //AuthTransfer refresh(String refreshToken, User user);
    OAuth2AccessToken refresh(String refreshToken, HttpServletRequest request)
            throws HttpRequestMethodNotSupportedException;
}
