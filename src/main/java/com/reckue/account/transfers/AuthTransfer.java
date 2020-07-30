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

    @JsonProperty(value = "accessToken")
    private String accessToken;

    @JsonProperty(value = "refreshToken")
    private String refreshToken;

    @JsonProperty(value = "tokenType")
    private String tokenType;

    @JsonProperty(value = "expiresIn")
    private long expiresIn;
}
