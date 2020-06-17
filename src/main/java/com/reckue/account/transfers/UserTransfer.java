package com.reckue.account.transfers;

import lombok.Data;

import java.util.Set;

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
