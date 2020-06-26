package com.reckue.account.controllers;

import com.reckue.account.services.UserService;
import com.reckue.account.transfers.UserTransfer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class UserСontroller represents a REST-Controller with get and delete operations connecting with users.
 *
 * @author Kamila Meshcheryakova
 */
@Api(tags = {"/users"})
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final Mapper mapper;
    private final UserService userService;

    /**
     * This type of request allows to get all the users that meet the requirements.
     *
     * @param limit  quantity of objects
     * @param offset quantity to skip
     * @param sort   parameter for sorting
     * @param desc   sorting descending
     * @return list of given quantity of objects of class UserTransfer with a given offset
     * sorted by the selected parameter for sorting in descending order
     */
    @ApiOperation(value = "View a list of available users", response = UserTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of users successfully retrieved"),
            @ApiResponse(code = 400, message = "You need to change the parameters of your request"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<UserTransfer> getAll(@RequestParam(required = false, defaultValue = "10") int limit,
                                     @RequestParam(required = false, defaultValue = "0") int offset,
                                     @RequestParam(required = false, defaultValue = "id") String sort,
                                     @RequestParam(required = false, defaultValue = "false") boolean desc) {
        List<UserTransfer> users = userService.findAll(limit, offset, sort, desc).stream()
                .map(userModel -> mapper.map(userModel, UserTransfer.class))
                .collect(Collectors.toList());
        log.debug("Retrieved all users by limit={}, offset={}, sort={}, desc={}", limit, offset, sort, desc);
        return users;
    }

    /**
     * This type of request allows to get the user by id.
     *
     * @param id the object identifier
     * @return the object of class UserTransfer
     */
    @ApiOperation(value = "Get user by id", response = UserTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The user successfully found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @GetMapping("/{id}")
    public UserTransfer getById(@PathVariable String id) {
        return mapper.map(userService.findById(id), UserTransfer.class);
    }

    /**
     * This type of request allows to get the user by name.
     *
     * @param username the object name
     * @return the object of class UserTransfer
     */
    @ApiOperation(value = "Get user by username", response = UserTransfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The user successfully found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @GetMapping("/username/{username}")
    public UserTransfer getByUsername(@PathVariable String username) {
        return mapper.map(userService.findByUsername(username), UserTransfer.class);
    }

    /**
     * This type of request allows to delete the user by id.
     *
     * @param id the object identifier
     */
    @ApiOperation(value = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The user successfully deleted"),
            @ApiResponse(code = 404, message = "The resource you were trying to delete is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteById(@PathVariable String id) {
        userService.deleteById(id);
    }

    /**
     * This type of request allows to delete the user by name.
     *
     * @param username the object name
     */
    @ApiOperation(value = "Delete user by username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The user successfully deleted"),
            @ApiResponse(code = 404, message = "The resource you were trying to delete is not found"),
            @ApiResponse(code = 500, message = "Access to the resource you tried to obtain is not possible")})
    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteByUsername(@PathVariable String username) {
        userService.deleteByUsername(username);
    }
}
