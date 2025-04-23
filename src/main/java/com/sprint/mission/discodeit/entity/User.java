package com.sprint.mission.discodeit.entity;


import java.io.Serializable;
import java.util.UUID;


// 사용자 << 공통 필드를 가진 Base 추상화 클래스 상속
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // 필드 정의
    private final UUID userId;                            // 사용자 고유 식별 번호
    private final Long createdAt;                        // 생성일
    private Long updatedAt;                             // 수정일
    private String userName;                           // 이름
    private String pwd;                               // 비밀번호
    private String email;                            // 주소
    private String phoneNumber;                     // 연락처
    private String statusMessage;                  // 상태 메세지

    // 생성자

    public User(String userName, String pwd, String email, String phoneNumber, String statusMessage) {
        this.userId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.userName = userName;
        this.pwd = pwd;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.statusMessage = statusMessage;
    }

    // Getter

    public UUID getUserId() {
        return userId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getUserName() {
        return userName;
    }

    public String getPwd() {
        return pwd;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatusMessage() {
        return statusMessage;
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
            this.updatedAt = System.currentTimeMillis();
        } else {
            // 예외 처리
            throw new IllegalArgumentException("No field to update");
        }
    }

}