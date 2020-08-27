package com.reckue.account.models;

import com.reckue.account.utils.helpers.RandomHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Class User represents the POJO-object and the main entity of the application.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@SuperBuilder
@AllArgsConstructor
@Entity
@Table(name = "users")
@SuppressWarnings("unused")
public class User {

    @Id
    private String id;

    private Status status;
    private String username;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "role_users", joinColumns = {
            @JoinColumn(name = "users_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    private String refreshToken;
    private long lastVisit;
    private long created;
    private long updated;

    public User() {
        Set<Role> set = new HashSet<>();
        set.add(new Role("ROLE_USER"));
        this.setId(RandomHelper.generate());
        this.status = Status.ACTIVE;
        this.roles = set;
        this.created = new Timestamp(System.currentTimeMillis()).getTime();
        this.updated = new Timestamp(System.currentTimeMillis()).getTime();
    }
}
