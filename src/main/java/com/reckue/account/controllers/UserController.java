package com.reckue.account.controllers;

import com.reckue.account.transfers.UserTransfer;
import com.reckue.account.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Mapper mapper;
    private final UserService userService;

    @Autowired
    public UserController(Mapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
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

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UserTransfer getById(@PathVariable String id) {
        return mapper.map(userService.findById(id), UserTransfer.class);
    }

    @GetMapping("/username/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public UserTransfer getByUsername(@PathVariable String username) {
        return mapper.map(userService.findByUsername(username), UserTransfer.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteById(@PathVariable String id) {
        userService.deleteById(id);
    }

    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteByUsername(@PathVariable String username) {
        userService.deleteByUsername(username);
    }
}
