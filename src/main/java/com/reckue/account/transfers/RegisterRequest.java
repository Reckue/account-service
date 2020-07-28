package com.reckue.account.transfers;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Class RegisterRequest represents an incoming DTO for a user to register.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class RegisterRequest {
    @NotNull
    @ApiModelProperty(notes = "The user name")
    private String username;

    @NotNull
    @ApiModelProperty(notes = "The user email, for example: email@mail.ru")
    private String email;

    @NotNull
    @ApiModelProperty(notes = "The user password, for example: Passw0rd")
    private String password;
}
