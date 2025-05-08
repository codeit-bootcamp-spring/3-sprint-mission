package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;
    private UUID profiledId;

    public User(String username, String email, String password,UUID profiledId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public void updatePassword(String oldPassword, String newPassword) {
        if(!oldPassword.equals(this.password)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if(newPassword==null){
            throw new IllegalArgumentException("새로운 비밀번호를 입력하지 않으셨습니다.");
        }
        if(newPassword.equals(this.password)){
            throw new IllegalArgumentException("이전 비밀번호와 새로운 비밀번호가 같습니다.");
        }

        this.password = newPassword;
        this.updatedAt = Instant.now();
    }

    public void updateProfiledId(UUID newProfiledId){
        if(newProfiledId != null){
            this.profiledId = newProfiledId;
            this.updatedAt = Instant.now();
        }
    }


}

