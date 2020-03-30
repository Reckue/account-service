package com.reckue.authorization.models.entities;

import com.reckue.authorization.utils.helpers.RandomHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Set;

@Document(collection = "users")
@Data
@SuperBuilder
@AllArgsConstructor
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
