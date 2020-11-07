package com.reckue.account.transfer;

import lombok.Data;

import java.util.Set;

/**
 * Class UserTransfer represents an outgoing DTO to receive an authorised user.
 *
 * @author Kamila Meshcheryakova
 */
@Data
public class UserTransfer {

    private String id;
    private String username;
    private String email;
    private Set<RoleTransfer> roles;
    private String status;
    private long created;
    private long updated;
    private long lastVisit;
}
