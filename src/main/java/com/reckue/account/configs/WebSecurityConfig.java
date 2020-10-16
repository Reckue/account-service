package com.reckue.account.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

/**
 * Class WebSecurityConfig contains web security configurations.
 *
 * @author Kamila Meshcheryakova
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final String[] AUTH_SWAGGER = {"/v2/api-docs", "/swagger-resources/**",
            "/", "/swagger-ui.html", "/webjars/**"};

    /**
     * This method is used to encode the password.
     *
     * @return an encoded password
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    /**
     * This method is used to expose the {@link AuthenticationManager} as a Bean.
     *
     * @return an object of AuthenticationManager class
     * @throws Exception all kind of checked exceptions
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable()
              //  .authorizeRequests()
               // .antMatchers("/auth/current").hasAuthority("ROLE_USER")
//                .antMatchers("/users/delete/**").hasRole("ADMIN")
               // .anyRequest().permitAll()
                //.and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (httpServletRequest, httpServletResponse, authExc) ->
                                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, String.valueOf(authExc)))
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) ->
                        httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, String.valueOf(e)));
    }
}
