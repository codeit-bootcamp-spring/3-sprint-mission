package com.sprint.mission.discodeit.auth.jwt.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

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

    public JwtTokenEntity(String jti, String username, String tokenType, OffsetDateTime issuedAt,
        OffsetDateTime expiresAt) {
        this.jti = jti;
        this.username = username;
        this.tokenType = tokenType;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public OffsetDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(OffsetDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public String getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    @Override
    public String toString() {
        return "JwtTokenEntity{" +
            "jti='" + jti + '\'' +
            ", username='" + username + '\'' +
            ", tokenType='" + tokenType + '\'' +
            ", issuedAt=" + issuedAt +
            ", expiresAt=" + expiresAt +
            ", revoked=" + revoked +
            ", replacedBy='" + replacedBy + '\'' +
            '}';
    }
}
