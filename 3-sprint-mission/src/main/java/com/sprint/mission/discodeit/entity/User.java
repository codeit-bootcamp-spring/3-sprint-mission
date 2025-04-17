package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class User {
    private final UUID id;
    private final String userid;
    private String password;
    private String name;
    private final Long createdAt;
    private Long updatedAt;
    private boolean isLogin;

    public User(String userid, String password, String name) {
        this.id = UUID.randomUUID();
        this.userid = userid;
        this.password = password;
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isLogin = false;
    }

    public UUID getId() {return id;}
    public String getUserId() {return userid;}
    public String getPassword() {return password;}
    public String getName() {return name;}
    public boolean getIsLogin() {return isLogin;}

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;}
    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;}

    @Override
    public String toString() {
        return "User{" +
//                "id=" + id +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", isLogin=" + isLogin +
                '}';
    }

    public void setLogin() {
        this.isLogin = true;
    }
    public void setLogout() {
        this.isLogin = false;
    }
    public void updateName(String name) {this.name = name;}
    public void updatePassword(String password) {this.password = password;}
    public void updateDateTime() {this.updatedAt = System.currentTimeMillis();}
}

