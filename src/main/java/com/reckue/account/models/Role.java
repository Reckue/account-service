package com.reckue.account.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * Class Role represents authentication of the user.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {

    private String name;

    /**
     * This method allows the user to obtain authentication.
     *
     * @return name of role
     */
    @Override
    public String getAuthority() {
        return this.name;
    }
}
