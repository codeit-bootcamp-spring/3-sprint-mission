package com.sprint.mission.discodeit.entitiy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String status;
    private Boolean isMikeOn;
    private Boolean isSpeakerOn;
    private Map<UUID,User> friends;

    public User(String userName, String password, String email, String phone, String status, Boolean isMikeOn, Boolean isSpeakerOn, Map<UUID,User> friends) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.isMikeOn = isMikeOn;
        this.isSpeakerOn = isSpeakerOn;
        this.friends = friends;
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", isMikeOn=" + isMikeOn +
                ", isSpeakerOn=" + isSpeakerOn +
                ", friends=" + friends +
                '}';
    }

    public UUID getId() {
        return id;
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

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getIsMikeOn() {
        return isMikeOn;
    }

    public Boolean getIsSpeakerOn() {return isSpeakerOn;}

    public Map<UUID,User> getFriends() {
        return friends;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateIsMikeOn(Boolean isMikeOn) {
        this.isMikeOn = isMikeOn;
    }

    public void updateIsSpeakerOn(Boolean isSpeakerOn) {
        this.isSpeakerOn = isSpeakerOn;
    }

    public void updateFriends(Map<UUID,User> friends) {
        this.friends = friends;
    }
}
