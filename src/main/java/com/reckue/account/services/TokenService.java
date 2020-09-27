package com.reckue.account.services;

import java.util.Map;

/**
 * Interface TokenService represents the service for getting additional information from a token.
 *
 * @author Kamila Meshcheryakova
 */
public interface TokenService {
    Map<String, Object> extractExtraInfo(String token);
}
