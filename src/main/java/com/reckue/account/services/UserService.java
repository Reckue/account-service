package com.reckue.account.services;

import com.reckue.account.exceptions.AccessDeniedException;
import com.reckue.account.exceptions.AlreadyExistsException;
import com.reckue.account.exceptions.NotFoundException;
import com.reckue.account.models.Role;
import com.reckue.account.models.Status;
import com.reckue.account.models.User;
import com.reckue.account.repositories.UserRepository;
import com.reckue.account.utils.helpers.RandomHelper;
import com.reckue.account.utils.helpers.TimestampHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class UserService represents service with operations related to the user and the database.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

    /**
     * This method is used to find all the users in the database that meet the requirements.
     *
     * @param limit  quantity of objects
     * @param offset quantity to skip
     * @param sort   parameter for sorting
     * @param desc   sorting descending or ascending
     * @return list of given quantity of objects of class UserTransfer with a given offset
     * sorted by the selected parameter for sorting in descending or ascending order
     */
    public List<User> findAll(int limit, int offset, String sort, boolean desc) {
        Sort sorted = desc ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        return userRepository.findAll(PageRequest.of(offset, limit, sorted)).getContent();
    }

    /**
     * This method is used to find the user by id in the database.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     *
     * @param id the object identifier
     * @return the object of class User
     */
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("The user by id '" + id + "' not found", HttpStatus.NOT_FOUND));
    }

    /**
     * This method is used to create an object of class User.
     * Throws {@link AlreadyExistsException} in case if user with such username or email already exists.
     *
     * @param userModel object of class User
     * @return the object of class User
     */
    public User create(User userModel) {
        if (!userRepository.existsByUsername(userModel.getUsername())
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

    /**
     * This method is user to delete the user by name.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     * Throws {@link AccessDeniedException} in case if the user isn't the same user or
     * hasn't admin authorities.
     *
     * @param username the object name
     */
    public void deleteByUsername(String username, String token) {
        if (!userRepository.existsByUsername(username)) {
            throw new NotFoundException("The user by username '" + username + "' not found", HttpStatus.NOT_FOUND);
        }
        Map<String, Object> tokenInfo = tokenStore.readAccessToken(token).getAdditionalInformation();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            String userId = user.get().getId();
            if (tokenInfo.get("userId").equals(userId) || tokenInfo.get("authorities").equals("ROLE_ADMIN")) {
                userRepository.deleteByUsername(username);
            } else {
                throw new AccessDeniedException("The operation forbidden", HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * This method is user to delete the user by id.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     * Throws {@link AccessDeniedException} in case if the user isn't the same user or
     * hasn't admin authorities.
     *
     * @param id the object identifier
     */
    public void deleteById(String id, String token) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("The user by id '" + id + "' not found", HttpStatus.NOT_FOUND);
        }
        Map<String, Object> tokenInfo = tokenStore.readAccessToken(token).getAdditionalInformation();
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            String userId = user.get().getId();
            if (tokenInfo.get("userId").equals(userId) || tokenInfo.get("authorities").equals("ROLE_ADMIN")) {
                userRepository.deleteById(id);
            } else {
                throw new AccessDeniedException("The operation forbidden", HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * This method is used to find the user by name in the database.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     *
     * @param username the object name
     * @return the object of class User
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("The user by username '" + username + "' not found",
                        HttpStatus.NOT_FOUND));
    }
}
