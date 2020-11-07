package com.reckue.account.controller;

import com.reckue.account.controller.api.UserApi;
import com.reckue.account.exception.AuthenticationException;
import com.reckue.account.service.UserService;
import com.reckue.account.transfer.UserTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Class UserСontroller represents a REST-Controller with get and delete operations connecting with users.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController implements UserApi {

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
    @GetMapping
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
    @GetMapping("/id/{id}")
    public UserTransfer getById(@PathVariable String id) {
        return mapper.map(userService.findById(id), UserTransfer.class);
    }

    /**
     * This type of request allows to get the user by name.
     *
     * @param username the object name
     * @return the object of class UserTransfer
     */
    @GetMapping("/username/{username}")
    public UserTransfer getByUsername(@PathVariable String username) {
        return mapper.map(userService.findByUsername(username), UserTransfer.class);
    }

    /**
     * This type of request allows to delete the user by id.
     * Throws {@link AuthenticationException} in case if token is absent.
     *
     * @param id the object identifier
     */
    @DeleteMapping("/delete/id/{id}")
    // @PreAuthorize("hasRole('ROLE_ADMIN')") doesn't work
    public void deleteById(@PathVariable String id, HttpServletRequest request) {
        try {
            String token = request.getHeader(AUTHORIZATION).substring(7);
            userService.deleteById(id, token);
        } catch (NullPointerException e) {
            throw new AuthenticationException("Token missing", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This type of request allows to delete the user by name.
     * Throws {@link AuthenticationException} in case if token is absent.
     *
     * @param username the object name
     */
    @DeleteMapping("/delete/username/{username}")
    //  @PreAuthorize("hasRole('ROLE_ADMIN')") doesn't work
    public void deleteByUsername(@PathVariable String username, HttpServletRequest request) {
        try {
            String token = request.getHeader(AUTHORIZATION).substring(7);
            userService.deleteByUsername(username, token);
        } catch (NullPointerException e) {
            throw new AuthenticationException("Token missing", HttpStatus.BAD_REQUEST);
        }
    }
}
