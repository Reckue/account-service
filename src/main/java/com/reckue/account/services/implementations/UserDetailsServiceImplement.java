package com.reckue.account.services.implementations;

import com.reckue.account.models.User;
import com.reckue.account.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class UserDetailsServiceImplement represents a UserDetailsService realization
 * which loads user-specific data.
 *
 * @author Kamila Meshcheryakova
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImplement implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * This method is used to locate the user based on the username.
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated user record
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User by username '" + username + "' not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(user.getRoles())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
