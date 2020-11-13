package com.reckue.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Class Role represents authentication of the user.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    public Role(String name) {
        this.name = name;
    }

    /**
     * This method allows the account to obtain authentication.
     *
     * @return name of role
     */
    @Override
    public String getAuthority() {
        return this.name;
    }
}
