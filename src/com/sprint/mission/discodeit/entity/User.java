package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;

public class User {
    private final UUID id = UUID.randomUUID();
    private String name;
    private String createdAt = new SimpleDateFormat("HH:mm:ss").format(currentTimeMillis());
    private String UpdatedAt;

    // UUID id | String name | String createdAt | String UpdatedAt |의 getter및 setter
    public UUID getId() { return id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getCreatedAt() { return createdAt;}
    public String getUpdatedAt() { return UpdatedAt;}
    public void setUpdatedAt(String updatedAt) {UpdatedAt = updatedAt;}


}

