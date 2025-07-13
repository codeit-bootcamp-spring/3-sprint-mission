package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.logging.log4j.util.Lazy;

import java.awt.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.entity
 * fileName       : User
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Getter
@Entity
@ToString
@NoArgsConstructor

@AllArgsConstructor
@Builder
@Table(name = "users", schema = "discodeit")
public class User extends BaseUpdatableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false,length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;


    // 프로필 없음
    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, BinaryContent profile) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    // 프로필 있음
    public User(String username, String email, String password, UserStatus status, BinaryContent profile) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = status;
        this.profile = profile;
    }

    // 프로필 없음
    public User(String username, String email, String password, UserStatus status) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = status;
    }

    public void changeUserStatus(UserStatus status) {
        this.status = status;
    }

    public void changeUsername(String username) {
        this.username = username;
    }
    public void changeEmail(String email) {
        this.email = email;
    }
    public void changePassword(String password) {
        this.password = password;
    }
    public void changeProfile(BinaryContent profile) {
        this.profile = profile;
    }
}
