package com.sprint.mission.discodeit.security.jwt.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tbl_jwt_token")
public class JwtTokenEntity {

    @Id
    @Column(name = "jti", length = 64)
    private String jti;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "token_type", nullable = false, length = 16)
    private String tokenType; // access | refresh

    @Column(name = "issued_at", nullable = false)
    private OffsetDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "replaced_by", length = 64)
    private String replacedBy;

    public JwtTokenEntity() {
    }

    public JwtTokenEntity(String jti, String username, String tokenType, OffsetDateTime issuedAt, OffsetDateTime expiresAt) {
        this.jti = jti;
        this.username = username;
        this.tokenType = tokenType;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
}
