package com.reckue.account.controller.api;

import com.reckue.account.transfer.AccountTransfer;
import com.reckue.account.transfer.RegisterRequest;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ResponseStatus;

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
    @ResponseStatus(code = HttpStatus.CREATED)
    AccountTransfer register(RegisterRequest registerForm);

    @ApiOperation(value = "Authorization", response = OAuth2AccessToken.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "JWT has created"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 404, message = "The account by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @ResponseStatus(code = HttpStatus.CREATED)
    ResponseEntity<OAuth2AccessToken> getToken(Principal principal, String scope, String grantType, String username,
                                               String password, String refreshToken) throws HttpRequestMethodNotSupportedException;


    @ApiOperation(value = "Get current user", response = AccountTransfer.class,
            authorizations = {@Authorization(value = "Bearer token")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has completed"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The account by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    AccountTransfer getCurrentUser(HttpServletRequest request);
}
