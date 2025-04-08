package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID userId;
    private String userName;
    private String email;
    private String password;
    private final long createdAt;
    private long updatedAt;

    public User(String userName, String email, String password) {
        this.userId = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.createdAt = System.currentTimeMillis(); // 현재 시간으로 생성 시간 초기화
        this.updatedAt = this.createdAt;
    }

    // getter
    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // 이메일 업데이트
    public void updateEmail(String email) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
            this.updatedAt = System.currentTimeMillis();
        }
    }

    // 사용자명 업데이트
    public void updateUserName(String userName) {
        if (userName != null && !userName.isEmpty()) {
            this.userName = userName;
        }
        this.updatedAt = System.currentTimeMillis();
    }

    //비밀번호 업데이트
    public void updatePassword(String password){
        if(password != null && !password.isEmpty()){
            this.password = password;
        }
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", userName=" + userName + ", email=" + email + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + ", password=" + password + "]";
    }
}
