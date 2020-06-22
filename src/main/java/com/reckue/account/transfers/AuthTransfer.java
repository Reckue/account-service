package com.reckue.account.transfers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class AuthTransfer represents an outgoing DTO to receive a user token.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTransfer {

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty(value = "expires_in")
    private long expiresIn;
}
