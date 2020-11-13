package com.reckue.account.controller.api;

import com.reckue.account.transfer.AccountTransfer;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Interface AccountApi allows to post annotations for swagger.
 *
 * @author Kamila Meshcheryakova
 */
@Api(tags = {"/accounts"})
@SuppressWarnings("unused")
public interface AccountApi {

    @ApiOperation(value = "View a list of available accounts", response = AccountTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of accounts successfully retrieved"),
            @ApiResponse(code = 400, message = "You need to change the parameters of your request"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    List<AccountTransfer> getAll(int limit, int offset, String sort, boolean desc);

    @ApiOperation(value = "Get account by id", response = AccountTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The account successfully found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    AccountTransfer getById(String id);

    @ApiOperation(value = "Get account by username", response = AccountTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The account successfully found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    AccountTransfer getByUsername(String username);

    @ApiOperation(value = "Delete account by id", authorizations = {@Authorization(value = "Bearer token")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The account successfully deleted"),
            @ApiResponse(code = 404, message = "The resource you were trying to delete is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    void deleteById(String id, HttpServletRequest request);

    @ApiOperation(value = "Delete account by username", authorizations = {@Authorization(value = "Bearer token")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The account successfully deleted"),
            @ApiResponse(code = 404, message = "The resource you were trying to delete is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    void deleteByUsername(String username, HttpServletRequest request);
}
