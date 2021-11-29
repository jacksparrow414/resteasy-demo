package com.demo.resteasy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.Principal;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User implements Principal {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "pass_word")
    private String password;
    @Column(name = "roles")
    private String roles;
    @Column(name = "scopes")
    private String scopes;


    @Override
    public String getName() {
        return getUserId();
    }
}
