package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -1421022282607757997L;
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        updateTime();
    }

    @Override
    public String toString() {
        return "User{name='" + userName + '\'' +
                ", createdAt=" + new Date(getCreatedAt()) +
                ", updatedAt=" + new Date(getUpdatedAt()) +
                '}';
    }
}
