package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseUpdatableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // 필드 정의
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    // BinaryContent 참조 ID
    // 단방향 참조
    @JoinColumn(name = "profile_id",
        foreignKey = @ForeignKey(name = "fk_user_profile", value = ConstraintMode.CONSTRAINT),
        nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private BinaryContent profile;

    // 양방향 참조
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    // 생성자
    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    // Update
    public void update(String newUserName, String newEmail, String newPassword,
        BinaryContent newProfile) {
        if (newUserName != null && !newUserName.equals(this.username)) {
            this.username = newUserName;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
        this.profile = newProfile;
    }

}