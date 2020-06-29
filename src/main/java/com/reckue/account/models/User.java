package com.reckue.account.models;

import com.reckue.account.utils.helpers.RandomHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Class User represents the POJO-object and the main entity of the application.
 *
 * @author Kamila Meshcheryakova
 */
@Data
@SuperBuilder
@AllArgsConstructor
@Document(collection = "users")
@SuppressWarnings("unused")
public class User {

    @Id
    private String id;

    private Status status;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
    private String refreshToken;
    private long lastVisit;
    private long created;
    private long updated;

    public User() {
        this.setId(RandomHelper.generate());
        this.status = Status.ACTIVE;
        this.created = new Timestamp(System.currentTimeMillis()).getTime();
        this.updated = new Timestamp(System.currentTimeMillis()).getTime();
    }
}
