package com.reckue.account.configs;

import com.reckue.account.configs.filters.AccessFilter;
import com.reckue.account.configs.filters.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * Class SecurityConfiguration sets the settings for security.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    /**
     * This method is used to add the filter at the location of the specified Filter class.
     *
     * @param builder web based security for specific http requests
     */
    @Override
    public void configure(HttpSecurity builder) {
        builder.addFilterBefore(new AccessFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}
