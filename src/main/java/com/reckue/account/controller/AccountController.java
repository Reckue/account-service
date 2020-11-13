package com.reckue.account.controller;

import com.reckue.account.controller.api.AccountApi;
import com.reckue.account.exception.AuthenticationException;
import com.reckue.account.service.AccountService;
import com.reckue.account.transfer.AccountTransfer;
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
 * Class AccountController represents a REST-Controller with get and delete operations connecting with users.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController implements AccountApi {

    private final Mapper mapper;
    private final AccountService accountService;

    /**
     * This type of request allows to get all the users that meet the requirements.
     *
     * @param limit  quantity of objects
     * @param offset quantity to skip
     * @param sort   parameter for sorting
     * @param desc   sorting descending
     * @return list of given quantity of objects of class AccountTransfer with a given offset
     * sorted by the selected parameter for sorting in descending order
     */
    @GetMapping
    public List<AccountTransfer> getAll(@RequestParam(required = false, defaultValue = "10") int limit,
                                        @RequestParam(required = false, defaultValue = "0") int offset,
                                        @RequestParam(required = false, defaultValue = "id") String sort,
                                        @RequestParam(required = false, defaultValue = "false") boolean desc) {
        List<AccountTransfer> accounts = accountService.findAll(limit, offset, sort, desc).stream()
                .map(account -> mapper.map(account, AccountTransfer.class))
                .collect(Collectors.toList());
        log.debug("Retrieved all accounts by limit={}, offset={}, sort={}, desc={}", limit, offset, sort, desc);
        return accounts;
    }

    /**
     * This type of request allows to get the account by id.
     *
     * @param id the object identifier
     * @return the object of class AccountTransfer
     */
    @GetMapping("/id/{id}")
    public AccountTransfer getById(@PathVariable String id) {
        return mapper.map(accountService.findById(id), AccountTransfer.class);
    }

    /**
     * This type of request allows to get the account by username.
     *
     * @param username the account username
     * @return the object of class AccountTransfer
     */
    @GetMapping("/username/{username}")
    public AccountTransfer getByUsername(@PathVariable String username) {
        return mapper.map(accountService.findByUsername(username), AccountTransfer.class);
    }

    /**
     * This type of request allows to delete the account by id.
     * Throws {@link AuthenticationException} in case if token is absent.
     *
     * @param id the object identifier
     */
    @DeleteMapping("/delete/id/{id}")
    // @PreAuthorize("hasRole('ROLE_ADMIN')") doesn't work
    public void deleteById(@PathVariable String id, HttpServletRequest request) {
        try {
            String token = request.getHeader(AUTHORIZATION).substring(7);
            accountService.deleteById(id, token);
        } catch (NullPointerException e) {
            throw new AuthenticationException("Token missing", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This type of request allows to delete the account by name.
     * Throws {@link AuthenticationException} in case if token is absent.
     *
     * @param username the object name
     */
    @DeleteMapping("/delete/username/{username}")
    //  @PreAuthorize("hasRole('ROLE_ADMIN')") doesn't work
    public void deleteByUsername(@PathVariable String username, HttpServletRequest request) {
        try {
            String token = request.getHeader(AUTHORIZATION).substring(7);
            accountService.deleteByUsername(username, token);
        } catch (NullPointerException e) {
            throw new AuthenticationException("Token missing", HttpStatus.BAD_REQUEST);
        }
    }
}
