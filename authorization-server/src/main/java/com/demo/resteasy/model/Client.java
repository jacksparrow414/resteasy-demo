package com.demo.resteasy.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client implements Serializable {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @Column(name = "scopes")
    private String scopes;

    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;
}
