package com.sprint.mission.discodeit.entitiy;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String status;
    private Boolean turnOnMike;
    private Boolean turnOnHeadset;
    private Map<UUID,User> friends;

    public User(String userName, String password, String email, String phone, String status, Boolean turnOnMike, Boolean turnOnHeadset, Map<UUID,User> friends) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.turnOnMike = turnOnMike;
        this.turnOnHeadset = turnOnHeadset;
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
                ", turnOnMike=" + turnOnMike +
                ", turnOnHeadset=" + turnOnHeadset +
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

    public Boolean getTurnOnMike() {
        return turnOnMike;
    }

    public Boolean getTurnOnHeadset() {
        return turnOnHeadset;
    }

    public Map<UUID,User> getFriends() {
        return friends;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
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

    public void updateTurnOnMike(Boolean turnOnMike) {
        this.turnOnMike = turnOnMike;
    }

    public void updateTurnOnHeadset(Boolean turnOnHeadset) {
        this.turnOnHeadset = turnOnHeadset;
    }

    public void updateFriends(Map<UUID,User> friends) {
        this.friends = friends;
    }
}
