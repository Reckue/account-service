package com.reckue.account.services;

import com.reckue.account.exceptions.AlreadyExistsException;
import com.reckue.account.exceptions.NotFoundException;
import com.reckue.account.models.Role;
import com.reckue.account.models.Status;
import com.reckue.account.models.User;
import com.reckue.account.repositories.UserRepository;
import com.reckue.account.utils.helpers.RandomHelper;
import com.reckue.account.utils.helpers.TimestampHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@Transactional
@SuppressWarnings("unused")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll(int limit, int offset, String sort, boolean desc) {
        Sort sorted = desc ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        return userRepository.findAll(PageRequest.of(offset, limit, sorted)).getContent();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("The user by id '" + id + "' not found", HttpStatus.NOT_FOUND));
    }

    public User create(User userModel) {
        if (userRepository.existsByUsername(userModel.getUsername())
                && !userRepository.existsByEmail(userModel.getEmail())) {
            User user = User.builder()
                    .id(RandomHelper.generate(userModel.getUsername()))
                    .username(userModel.getUsername())
                    .email(userModel.getEmail())
                    .password(passwordEncoder.encode(userModel.getPassword()))
                    .roles(new HashSet<>())
                    .status(Status.ACTIVE)
                    .created(TimestampHelper.getCurrentTimestamp())
                    .updated(TimestampHelper.getCurrentTimestamp())
                    .build();
            user.getRoles().add(new Role("ROLE_USER"));
            return userRepository.save(user);
        } else {
            throw new AlreadyExistsException("Username or Email already exists", HttpStatus.NOT_MODIFIED);
        }
    }

    public void deleteByUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            userRepository.deleteByUsername(username);
        } else {
            throw new NotFoundException("The user by username '" + username + "' not found", HttpStatus.NOT_FOUND);
        }
    }

    public void deleteById(String id) {
        if (!userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException("The user by username '" + id + "' not found", HttpStatus.NOT_FOUND);
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("The user by username '" + username + "' not found",
                        HttpStatus.NOT_FOUND));
    }
}
