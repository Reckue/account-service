package com.reckue.account.transfers;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Class LoginRequest represents an incoming DTO for a user to log in.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull
    @ApiModelProperty(notes = "The user name")
    private String username;

    @NotNull
    @ApiModelProperty(notes = "The user password")
    private String password;
}
