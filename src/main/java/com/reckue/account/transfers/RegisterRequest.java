package com.reckue.account.transfers;

import com.reckue.account.exceptions.InvalidDataException;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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

    public void setPassword(String password) {
        if (password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}")) {
            this.password = password;
        }
        else {
            throw new InvalidDataException("Your password must contain at least 8 chars" +
                    ", one digit" +
                    ", one lower alpha char and one upper alpha char" +
                    ", and not contain space, tab, etc.", HttpStatus.BAD_REQUEST);
        }
    }

    public void setEmail(String email) {
        if (email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b")) {
            this.email = email;
        }
        else {
            throw new InvalidDataException("Please correct, it isn't an email.", HttpStatus.BAD_REQUEST);
        }
    }
}
