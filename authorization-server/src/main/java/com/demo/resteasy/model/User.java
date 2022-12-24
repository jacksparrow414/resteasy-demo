package com.demo.resteasy.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.security.Principal;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User implements Serializable {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "pass_word")
    private String password;
    @Column(name = "roles")
    private String roles;
    @Column(name = "scopes")
    private String scopes;
}
