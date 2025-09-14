package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;

/**
 * 사용자 정보를 관리하는 엔티티 클래스입니다.
 * 
 * <p>사용자의 기본 정보, 프로필 이미지, 권한 등을 관리하며,
 * BaseUpdatableEntity를 상속받아 생성/수정 시간을 자동으로 기록합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>사용자명과 이메일의 고유성 보장</li>
 *   <li>프로필 이미지와 1:1 관계</li>
 *   <li>역할 기반 권한 관리</li>
 *   <li>동적 업데이트 지원</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Entity
@Table(name = "tbl_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class User extends BaseUpdatableEntity {

    /**
     * 사용자명입니다.
     * 
     * <p>50자 이하의 고유한 값으로, 사용자 식별에 사용됩니다.</p>
     */
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    /**
     * 사용자의 이메일 주소입니다.
     * 
     * <p>100자 이하의 고유한 값으로, 로그인 및 연락처로 사용됩니다.</p>
     */
    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    /**
     * 사용자의 암호화된 비밀번호입니다.
     * 
     * <p>BCrypt 등으로 암호화되어 저장되며, 평문으로는 접근할 수 없습니다.</p>
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 사용자의 프로필 이미지입니다.
     * 
     * <p>BinaryContent와 1:1 관계로 연결되며, 사용자 삭제 시 함께 제거됩니다.</p>
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) // 1:0..1
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    /**
     * 사용자의 권한 역할입니다.
     * 
     * <p>USER, CHANNEL_MANAGER, ADMIN 중 하나의 값을 가지며, 접근 제어에 사용됩니다.</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * 새로운 사용자를 생성합니다.
     * 
     * @param username 사용자명
     * @param email 이메일 주소
     * @param password 암호화된 비밀번호
     * @param profile 프로필 이미지 (선택사항)
     */
    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = Role.USER;
    }

    /**
     * 사용자 정보를 업데이트합니다.
     * 
     * <p>변경된 값이 있는 경우에만 수정 시간을 갱신합니다.</p>
     * 
     * @param newUsername 새로운 사용자명 (변경하지 않으려면 null)
     * @param newEmail 새로운 이메일 주소 (변경하지 않으려면 null)
     * @param newPassword 새로운 비밀번호 (변경하지 않으려면 null)
     * @param newProfile 새로운 프로필 이미지 (변경하지 않으려면 null)
     */
    public void update(String newUsername, String newEmail, String newPassword,
        BinaryContent newProfile) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }

    /**
     * 사용자의 권한 역할을 업데이트합니다.
     * 
     * <p>역할이 변경된 경우에만 수정 시간을 갱신합니다.</p>
     * 
     * @param newRole 새로운 권한 역할
     */
    public void updateRole(Role newRole) {
        boolean anyValueUpdated = false;
        if (newRole != null) {
            this.role = newRole;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }
}
