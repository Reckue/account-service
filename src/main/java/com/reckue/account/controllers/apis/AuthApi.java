package com.reckue.account.controllers.apis;

import com.reckue.account.transfers.AuthTransfer;
import com.reckue.account.transfers.LoginRequest;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.transfers.UserTransfer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface AuthApi allows to post annotations for swagger.
 *
 * @author Kamila Meshcheryakova
 */
@Api(tags = "/auth")
public interface AuthApi {

    @ApiOperation(value = "Registration", response = AuthTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Register form has created"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    AuthTransfer register(RegisterRequest registerForm);

    @ApiOperation(value = "Authorization", response = AuthTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Login form has accepted"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 404, message = "The user by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    AuthTransfer login(LoginRequest loginForm);

    @ApiOperation(value = "Get current user", response = UserTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "The request has accepted"),
            @ApiResponse(code = 400, message = "You need to change the incoming parameters"),
            @ApiResponse(code = 404, message = "The user by this username is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    UserTransfer getCurrentUser(HttpServletRequest request);

    @ApiOperation(value = "Updating", response = AuthTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "RefreshToken has updated"),
            @ApiResponse(code = 400, message = "Invalid incoming parameters"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    AuthTransfer refresh(String refreshToken, User user);
}
