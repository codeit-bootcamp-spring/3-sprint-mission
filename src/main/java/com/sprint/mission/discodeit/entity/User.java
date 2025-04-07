package com.sprint.mission.discodeit.entity;

// 활동 상태는 login & logout 메서드 생성해서 변환 작업 (Service)

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// 사용자 << 공통 필드를 가진 Base 추상화 클래스 상속
public class User {
    // 필드 정의
    private final UUID userId;                            // 사용자 고유 식별 번호
    private final long createdAt;                        // 생성일
    private long updatedAt;                             // 수정일
    private String userName;                           // 이름
    private String pwd;                               // 비밀번호
    private String email;                            // 주소
    private String phoneNumber;                     // 연락처
    private String statusMessage;                  // 상태 메세지
    private boolean activeStatus;                 // 활동 상태 ( on / off ) | 기본 off

    // 생성자

    public User(String userName, String pwd, String email, String phoneNumber, String statusMessage, boolean activeStatus, long updatedAt) {
        this.userId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.userName = userName;
        this.pwd = pwd;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.statusMessage = statusMessage;
        this.activeStatus = activeStatus;
        this.updatedAt = this.createdAt;
    }

    // Getter

    public UUID getUserId() {
        return userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
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

    public boolean isActiveStatus() {
        return activeStatus;
    }


    // Setter

    public void setUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }


    // toString()

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userName='" + userName + '\'' +
                ", pwd='" + pwd + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                ", activeStatus=" + activeStatus +
                '}';
    }


    // 타임스탬프( 생성 : User Create TimeStamp )
    public String getFormattedCreatedAt() {
        Date dateC = new Date(this.createdAt);
        SimpleDateFormat uct = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return uct.format(dateC);
    }

    // 수정 ( User Update TimeStamp )
    public String getFormattedUpdatedAt() {
        Date dateU = new Date(this.updatedAt);
        SimpleDateFormat uut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return uut.format(dateU);
    }

}