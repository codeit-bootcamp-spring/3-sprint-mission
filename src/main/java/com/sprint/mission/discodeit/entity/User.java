package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


// 사용자 << 공통 필드를 가진 Base 추상화 클래스 상속

// Lombok( 해당 클래스의 필드에만 적용됨 | Getter 메서드를 사용하던 시점에 비해 코드 가독성 증진 )
@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // 필드 정의
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;
    private String userName;
    private String pwd;
    private String email;
    private String phoneNumber;
    private String statusMessage;
    // BinaryContent 참조 ID
    private UUID profileId;

    // 생성자
    public User(String userName, String pwd, String email, String phoneNumber, String statusMessage, UUID profileId) {
        this.userId = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userName = userName;
        this.pwd = pwd;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.statusMessage = statusMessage;
        //
        this.profileId = profileId;
    }


    // Update
    public void update(String newUserName, String newPwd, String newEmail, String newPhoneNumber, String newStatusMessage) {
        boolean updated = false;
        if (newUserName != null && !newUserName.equals(this.userName)) {
            this.userName = newUserName;
            updated = true;
        }
        if (newPwd != null && !newPwd.equals(this.pwd)) {
            this.pwd = newPwd;
            updated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            updated = true;
        }
        if (newPhoneNumber != null && !newPhoneNumber.equals(this.phoneNumber)) {
            this.phoneNumber = newPhoneNumber;
        }
        if (newStatusMessage != null && !newStatusMessage.equals(this.statusMessage)) {
            this.statusMessage = newStatusMessage;
        }
        if (updated) {
            this.updatedAt = Instant.now();
        } else {
            // 예외 처리
            throw new IllegalArgumentException("No field to update");
        }
    }

}