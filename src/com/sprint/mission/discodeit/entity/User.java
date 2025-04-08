package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class User {
    private final UUID id = UUID.randomUUID();
    private long createdAt  = System.currentTimeMillis();
    private long updatedAt;
    private String name;

    public User(String name, long createdAt, long updatedAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {return id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getCreatedAt() {
        String formatedTime = new SimpleDateFormat("MM.dd HH:mm:ss").format(createdAt);
        return formatedTime;}
    public String getUpdatedAt() {
        String formatedTime = new SimpleDateFormat("MM.dd HH:mm:ss").format(updatedAt);
        return formatedTime;}
    public void setUpdatedAt(long updatedAt) {this.updatedAt = updatedAt;}

@Override
public String toString() {
    String result;
    if(getCreatedAt().equals(getUpdatedAt())){
        result = "\n 사용자 이름 : " + name + "\n 생성된 시점 : " + getCreatedAt() + "\n 사용자 UUID : " + id + "\n";
    } else {
        result = "\n 사용자 이름 : " + name + "\n 생성된 시점 : " + getCreatedAt() + "\n 수정된 시점 : " + getUpdatedAt() + "\n 사용자 UUID : " + id + "\n";
    }
    return result;
    }



}
