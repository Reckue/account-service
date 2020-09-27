package com.reckue.account.services;

import com.reckue.account.exceptions.AuthenticationException;
import com.reckue.account.exceptions.InvalidDataException;
import com.reckue.account.exceptions.NotFoundException;
import com.reckue.account.models.Role;
import com.reckue.account.models.Status;
import com.reckue.account.models.User;
import com.reckue.account.repositories.UserRepository;
import com.reckue.account.transfers.RegisterRequest;
import com.reckue.account.utils.helpers.RandomHelper;
import com.reckue.account.utils.helpers.TimestampHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Class AuthService represents service with operations related to authentication and authorization.
 *
 * @author Kamila Meshcheryakova
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

//    private final TokenProvider tokenProvider;
    private final DefaultTokenServices tokenServices;
    private final TokenStore tokenStore;
    private final TokenEndpoint tokenEndpoint;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;
    private  final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * This method is used to register a new user.
     * Throws {@link AuthenticationException} in case if user with such username already exists.
     *
     * @param registerForm with required fields
     * @return the object of class AuthTransfer
     */
//    public AuthTransfer register(RegisterRequest registerForm) {
    public void register(RegisterRequest registerForm) {
        // checking that the user exists in the database
        if (!userRepository.existsByUsername(registerForm.getUsername())) {

            // check password verification
            if (!registerForm.getPassword().matches("(.*).{6,}")) {
                throw new InvalidDataException("Your password must contain at least 6 any symbols.",
                        HttpStatus.BAD_REQUEST);
            }
            // check email verification
            if (!registerForm.getEmail().matches("\\b[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}\\b")) {
                throw new InvalidDataException("Please correct, it isn't an email. Use lowercase letters.",
                        HttpStatus.BAD_REQUEST);
            }
//
//            // create a new refresh token
//            String refreshToken = tokenProvider.createRefreshToken();

            // create instance of user model and fill it
            User user = User.builder()
                    .id(RandomHelper.generate(registerForm.getUsername()))
                    .username(registerForm.getUsername())
                    .email(registerForm.getEmail())
                    .password(passwordEncoder.encode(registerForm.getPassword()))
//                    .refreshToken(refreshToken)
                    .roles(new HashSet<>())
                    .status(Status.ACTIVE)
                    .created(TimestampHelper.getCurrentTimestamp())
                    .updated(TimestampHelper.getCurrentTimestamp())
                    .lastVisit(TimestampHelper.getCurrentTimestamp())
                    .build();

            // add roles to instance of user model
            user.getRoles().add(new Role("ROLE_USER"));

            // save the user in database
            userRepository.save(user);

            // create tokens transfer object and return it
//            return AuthTransfer.builder()
//                    .accessToken(tokenProvider.createAccessToken(user.getUsername(), user.getRoles()))
//                    .refreshToken(refreshToken)
//                    .tokenType(tokenProvider.getTokenType())
//                    .expiresIn(tokenProvider.getExpire())
//                    .build();
        } else {
            throw new AuthenticationException("Username already exists", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method is used an authorized user to log in.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     * Throws {@link AuthenticationException} in case if user enters invalid username or password.
     *
     * @param loginForm with required fields
     * @return the object of class AuthTransfer
     */
//    public AuthTransfer login(LoginRequest loginForm) {
//        try {
//            // find user from database
//            User user = userRepository.findByUsername(loginForm.getUsername()).orElseThrow(() ->
//                    new NotFoundException("The user by username [" + loginForm.getUsername() + "] not found",
//                            HttpStatus.NOT_FOUND));
//
//            // create a new refresh token
//            //String refreshToken = tokenProvider.createRefreshToken();
//            String refreshToken = tokenService.extractExtraInfo()
//
//                    // update refresh token
//            user.setRefreshToken(refreshToken);
//
//            // update last visit date
//            user.setLastVisit(TimestampHelper.getCurrentTimestamp());
//
//            // update user details in database
//            userRepository.save(user);
//
//            // authenticate this user in the authentication manager
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(),
//                    loginForm.getPassword()));
//
//            // create tokens transfer object and return it
//            return AuthTransfer.builder()
//                    .accessToken(tokenProvider.createAccessToken(user.getUsername(), user.getRoles()))
//                    .refreshToken(refreshToken)
//                    .tokenType(tokenProvider.getTokenType())
//                    .expiresIn(tokenProvider.getExpire())
//                    .build();
//        } catch (AuthenticationException e) {
//            throw new AuthenticationException("Invalid username or password supplied", HttpStatus.BAD_REQUEST);
//        }
//    }
    public ResponseEntity<OAuth2AccessToken> login (ResponseEntity<OAuth2AccessToken> responseEntity) {
        String userId = (String) Objects.requireNonNull(responseEntity.getBody()).getAdditionalInformation().get("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user by userId [" + userId + "] not found",
                        HttpStatus.NOT_FOUND));

        // update last visit date
        user.setLastVisit(TimestampHelper.getCurrentTimestamp());
        user.setRefreshToken(responseEntity.getBody().getRefreshToken().toString());

        return responseEntity;
    }
    /**
     * This method is used to get the user by his token.
     * Throws {@link NotFoundException} in case if such user isn't contained in database.
     *
     * @param request information for HTTP servlets
     * @return the object of class UserTransfer
     */
    public User getCurrentUser(HttpServletRequest request) {
        // get username from jwt token
      //  String username = tokenProvider.getUsernameByToken(tokenProvider.extractToken(request));
        String token = request.getHeader(AUTHORIZATION).substring(7);
        String userId = (String) tokenStore.readAccessToken(token).getAdditionalInformation().get("userId");

        // find user by username from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user by userId [" + userId + "] not found",
                        HttpStatus.NOT_FOUND));

        // update last visit date
        user.setLastVisit(TimestampHelper.getCurrentTimestamp());

        return user;
    }

    /**
     * This method is used to update the token of an authorized user.
     * Throws {@link AuthenticationException} in case if an invalid refresh token is entered.
     *
     * @param username     name of user
     * @param refreshToken token of an authorized user
     * @return the object of class AuthTransfer
     */
    public OAuth2AccessToken refresh(String refreshToken, HttpServletRequest request)
            throws HttpRequestMethodNotSupportedException {
        OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(
                tokenStore.readRefreshToken(refreshToken));
        // get instance of user model by username from database
        User userModel = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("The user by username [" + authentication.getName() + "] not found",
                        HttpStatus.NOT_FOUND));


        // check for equality of refresh tokens
        if (userModel.getRefreshToken().equals(refreshToken)) {

            // update last visit date
            userModel.setLastVisit(TimestampHelper.getCurrentTimestamp());

            String token = request.getHeader(AUTHORIZATION).substring(7);

            Map<String, Object> info = tokenService.extractExtraInfo(token);


            //Map<String, Object> info = tokenStore.readAccessToken(token).getAdditionalInformation();

            OAuth2AccessToken accessToken2 = tokenServices.createAccessToken(authentication);

            DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken2);
            customAccessToken.setAdditionalInformation(info);


            //tokenServices.createAccessToken(authentication);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("scope", "write");
            parameters.put("grant_type", "refresh_token");
            parameters.put("username", userModel.getUsername());
            parameters.put("password",userModel.getPassword());


           return //customAccessToken;
           tokenServices.refreshAccessToken(refreshToken, new TokenRequest(parameters, "client",
                          Collections.singletonList("write"), "refresh_token"));
           //tokenServices.createAccessToken(authentication);
               //  tokenEndpoint.postAccessToken(authentication, parameters).getBody();
                   //tokenStore.getAccessToken(authentication);
        } else {
            throw new AuthenticationException("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }
    }

    public OAuth2AccessToken  refreshToken(String refreshToken, String grantType, Principal principal, HttpServletRequest request) throws HttpRequestMethodNotSupportedException {
        OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(
                tokenStore.readRefreshToken(refreshToken));
        // get instance of user model by username from database
        User userModel = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("The user by username [" + authentication.getName() + "] not found",
                        HttpStatus.NOT_FOUND));
        String token = request.getHeader(AUTHORIZATION).substring(7);
//        String username = (String) tokenStore.readAccessToken(token).getAdditionalInformation().get("username");
//        User userModel = userRepository.findByUsername(username)
//                .orElseThrow(() -> new NotFoundException("The user by username [" + username + "] not found",
//                        HttpStatus.NOT_FOUND));

        // check for equality of refresh tokens
        if (userModel.getRefreshToken().equals(refreshToken)) {

            // update last visit date
            userModel.setLastVisit(TimestampHelper.getCurrentTimestamp());

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("client_id", "client");
            parameters.put("grant_type", grantType);
            parameters.put("password", userModel.getUsername());
            parameters.put("scope", "write");
            parameters.put("username", userModel.getPassword());
            parameters.put("token", token);

            AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(parameters);
            authorizationRequest.setApproved(true);

            OAuth2Request oauth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);

          //  Authentication authentication1 = (Authentication) principal;

            log.info("principal = [{}], user = [{}]", principal, userModel);
            // Create principal and auth token
//            final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(
//                    userModel.getUsername(), userModel.getPassword());
            Authentication auth = (Authentication) principal;
 //          Authentication authentication2 = authenticationManager.authenticate(loginToken);

            OAuth2Authentication authenticationRequest = new OAuth2Authentication(oauth2Request, auth);
            authenticationRequest.setAuthenticated(true);



            return tokenEndpoint.postAccessToken(principal, parameters).getBody();
                  //  tokenServices.createAccessToken(authentication); //Рабочий но пустой список
                  // tokenServices.createAccessToken(authenticationRequest); //Рабочий но пустой список
           // tokenServices.refreshAccessToken(refreshToken, oauth2Request.getRefreshTokenRequest()); // clientId = null
//                    tokenServices.refreshAccessToken(refreshToken, new TokenRequest(parameters, "client",
//                    Collections.singletonList("write"), grantType)); // empty access token and the same refresh
        } else {
            throw new AuthenticationException("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }
    }

//    public AuthTransfer refresh(String username, String refreshToken) {
//        // get instance of user model by username from database
//        User userModel = userRepository.findByUsername(username)
//                .orElseThrow(() -> new NotFoundException("The user by username [" + username + "] not found",
//                        HttpStatus.NOT_FOUND));
//
//        // check for equality of refresh tokens
//        if (userModel.getRefreshToken().equals(refreshToken)) {
//
//            // update last visit date
//            userModel.setLastVisit(TimestampHelper.getCurrentTimestamp());
//
//            return AuthTransfer.builder()
//                    .accessToken(tokenProvider.createAccessToken(username, userModel.getRoles()))
//                    .refreshToken(tokenProvider.createRefreshToken())
//                    .tokenType(tokenProvider.getTokenType())
//                    .expiresIn(tokenProvider.getExpire())
//                    .build();
//        } else {
//            throw new AuthenticationException("Invalid refresh token", HttpStatus.BAD_REQUEST);
//        }
//    }
}
