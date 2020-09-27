//package com.reckue.account.configs.filters;
//
//import com.reckue.account.exceptions.AuthenticationException;
//import com.reckue.account.models.Role;
//import com.reckue.account.utils.helpers.RandomHelper;
//import io.jsonwebtoken.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.http.HttpServletRequest;
//import java.sql.Timestamp;
//import java.util.Base64;
//import java.util.Date;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * Class TokenProvider provides a token.
// *
// * @author Kamila Meshcheryakova
// */
//@Slf4j
////@Component
//public class TokenProvider {
//
//    @Value("${security.token.secret:secret-key}")
//    private String secretKey;
//
//    @Value("${security.token.expires-in:3600000}")
//    private final long expire = 3600000;
//
//    @Value("${security.token.token-type:bearer}")
//    private final String tokenType = "bearer";
//
//    private final UserDetailsService userDetailsService;
//
//    @Autowired
//    public TokenProvider(@Qualifier("userDetailsServiceImplement") UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    /**
//     * This method is used to init a secretKey using the encoder.
//     */
//    @PostConstruct
//    protected void init() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//    }
//
//    /**
//     * This method is used to create access token.
//     *
//     * @param username the name of user
//     * @param roles    list of user roles
//     * @return a token
//     */
//    public String createAccessToken(String username, Set<Role> roles) {
//        Claims claims = Jwts.claims().setSubject(username);
//        claims.put("auth", roles.stream()
//                .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
//                .collect(Collectors.toList()));
//        Date currentDate = new Date();
//        Date expiresInDate = new Date(currentDate.getTime() + expire);
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(currentDate)
//                .setExpiration(expiresInDate)
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }
//
//    /**
//     * This method is used to create a refresh token.
//     *
//     * @return refresh token
//     */
//    public String createRefreshToken() {
//        return RandomHelper.generate();
//    }
//
//    /**
//     * This method is used to set token validity.
//     *
//     * @return validity as long type
//     */
//    public long getExpire() {
//        return new Timestamp(System.currentTimeMillis()).getTime() + expire;
//    }
//
//    /**
//     * Getter for token type.
//     *
//     * @return token type as string
//     */
//    public String getTokenType() {
//        return tokenType;
//    }
//
//    /**
//     * This method is used to authenticate user by token.
//     *
//     * @param token the user token
//     * @return a simple presentation of a username and password
//     */
//    public Authentication authenticateToken(String token) {
//        UserDetails userDetails;
//        String username = getUsernameByToken(token);
//        try {
//            userDetails = userDetailsService.loadUserByUsername(username);
//            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//        } catch (UsernameNotFoundException e) {
//            throw new AuthenticationException("The username " + username + " not found", HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    /**
//     * This method is used to get user name by token.
//     * Throws {@link AuthenticationException} in case:
//     * if a token has expired;
//     * if an invalid token is entered;
//     * if a token is incorrect - a JWT was not correctly constructed and should be rejected;
//     * if a signature or verifying an existing signature of a JWT failed;
//     * if a method has been passed an illegal or inappropriate argument.
//     *
//     * @param token the user token
//     * @return a user name
//     */
//    public String getUsernameByToken(String token) {
//        try {
//            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//        } catch (ExpiredJwtException e) {
//            throw new AuthenticationException("Expired token", HttpStatus.UNAUTHORIZED);
//        } catch (UnsupportedJwtException e) {
//            throw new AuthenticationException("Invalid token", HttpStatus.BAD_REQUEST);
//        } catch (MalformedJwtException e) {
//            throw new AuthenticationException("Wrong token", HttpStatus.BAD_REQUEST);
//        } catch (SignatureException e) {
//            throw new AuthenticationException("Unverified token", HttpStatus.BAD_REQUEST);
//        } catch (IllegalArgumentException e) {
//            throw new AuthenticationException("Illegal argument", HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    /**
//     * This method is used to get token without token type.
//     *
//     * @param req request information for HTTP servlets
//     * @return a token without token type
//     */
//    public String extractToken(HttpServletRequest req) {
//        String token = req.getHeader("Authorization");
//
//        if (token != null && token.startsWith("Bearer ")) {
//            return token.substring(7);
//        }
//        return null;
//    }
//
//    /**
//     * This method is used to validate token by secret key.
//     * Throws {@link AuthenticationException} in case:
//     * if a token has expired;
//     * if an invalid token is entered;
//     * if a token is incorrect - a JWT was not correctly constructed and should be rejected;
//     * if a signature or verifying an existing signature of a JWT failed;
//     * if a method has been passed an illegal or inappropriate argument.
//     *
//     * @param token the user token
//     * @return true or false
//     */
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException e) {
//            throw new AuthenticationException("Expired token", HttpStatus.UNAUTHORIZED);
//        } catch (UnsupportedJwtException e) {
//            throw new AuthenticationException("Invalid token", HttpStatus.BAD_REQUEST);
//        } catch (MalformedJwtException e) {
//            throw new AuthenticationException("Wrong token", HttpStatus.BAD_REQUEST);
//        } catch (SignatureException e) {
//            throw new AuthenticationException("Unverified token", HttpStatus.BAD_REQUEST);
//        } catch (IllegalArgumentException e) {
//            throw new AuthenticationException("Illegal argument", HttpStatus.BAD_REQUEST);
//        }
//    }
//}
