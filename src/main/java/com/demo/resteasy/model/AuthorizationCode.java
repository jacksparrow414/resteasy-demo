package com.demo.resteasy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "authorization_code")
@Getter
@Setter
public class AuthorizationCode {
    /**
     * 这里ID自己传入，不用生成策略
     */
    @Id
    @Column(name = "code")
    private String code;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "approved_scopes")
    private String approvedScopes;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
}
