package com.reckue.account.model;

import com.reckue.account.util.helper.RandomHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class Account represents the POJO-object and the main entity of the application.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@SuperBuilder
@AllArgsConstructor
@Entity
@Table(name = "accounts")
@SuppressWarnings("unused")
public class Account implements UserDetails {

    @Id
    private String id;

    private Status status;
    private String username;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "role_accounts", joinColumns = {
            @JoinColumn(name = "accounts_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    @Column(length = 500)
    private String refreshToken;
    private long lastVisit;
    private long created;
    private long updated;

    public Account() {
        Set<Role> set = new HashSet<>();
        set.add(new Role("ROLE_USER"));
        this.setId(RandomHelper.generate());
        this.status = Status.ACTIVE;
        this.roles = set;
        this.created = new Timestamp(System.currentTimeMillis()).getTime();
        this.updated = new Timestamp(System.currentTimeMillis()).getTime();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
