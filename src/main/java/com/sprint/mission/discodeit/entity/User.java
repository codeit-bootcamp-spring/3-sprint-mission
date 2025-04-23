package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id = UUID.randomUUID();
    private final long createdAt;
    private long updatedAt;
    private String name;

    public User(String name, long createdAt, long updatedAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(createdAt);
    }
    public String getUpdatedAt() {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(updatedAt);
    }
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        String result;
        if(getCreatedAt().equals(getUpdatedAt())){
            result = "\n 사용자 이름 : " + name + "\n 생성일 : " + getCreatedAt() + "\n 사용자 UUID : " + id + "\n";
        } else {
            result = "\n 사용자 이름 : " + name + "\n 생성일 : " + getCreatedAt() + "\n 수정일 : " + getUpdatedAt() + "\n 사용자 UUID : " + id + "\n";
        }
        return result;
        }
}
