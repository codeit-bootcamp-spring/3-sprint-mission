package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * PackageName  : com.sprint.mission.discodeit.entity
 * FileName     : JwtTokenEntity
 * Author       : dounguk
 * Date         : 2025. 8. 14.
 */
@Entity
@Table(name = "tokens")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    // 폐기 여부
    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    // 회전 시 새 리프레시 토큰의 jti
    @Column(name = "replaced_by", length = 64)
    private String replacedBy;


    public JwtTokenEntity(String jti, String username, String tokenType, OffsetDateTime issuedAt, OffsetDateTime expiresAt) {
        this.jti = jti;
        this.username = username;
        this.tokenType = tokenType;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
}
