package com.reckue.account.configs;

import com.reckue.account.configs.filters.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Class WebSecurityConfiguration sets the settings for web security.
 *
 * @author Kamila Meshcheryakova
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    /**
     * This method is used to configure the access for authorised and unauthorised users.
     *
     * @param http web based security for specific http requests
     * @throws Exception all kind of checked exceptions
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and();

        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Entry points
        http.authorizeRequests()//
                .antMatchers("/login").permitAll()//
                .antMatchers("/register").permitAll()//
                .antMatchers("/users/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/demo").permitAll()
                .antMatchers("/h2-console/**/**").permitAll()

                // Disallow everything else..
                .anyRequest().authenticated();

        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().accessDeniedPage("/error");

        // Apply tokenProvider
        http.apply(new SecurityConfiguration(tokenProvider));

        // Optional, if you want to test the API from a browser
        // http.httpBasic();
    }

    /**
     * This method is used to configure the access to swagger without authentication.
     *
     * @param web an instance of web security
     */
    @Override
    public void configure(WebSecurity web) {
        // Allow swagger to be accessed without authentication
        web.ignoring().antMatchers("/v2/api-docs")//
                .antMatchers("/swagger-resources/**")//
                .antMatchers("/swagger-ui.html")//
                .antMatchers("/configuration/**")//
                .antMatchers("/webjars/**")//
                .antMatchers("/public")

                // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
                .and()
                .ignoring()
                .antMatchers("/h2-console/**/**");
    }

    /**
     * This method is used to provide an instance based on the provided request.
     *
     * @return an object of UrlBasedCorsConfigurationSource class
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    /**
     * This method is used to encode the password.
     *
     * @return an encoded password
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * This method is used to expose the {@link AuthenticationManager} as a Bean.
     *
     * @return an object of AuthenticationManager class
     * @throws Exception all kind of checked exceptions
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
