package com.sprint.mission.discodeit.entity;


import java.util.UUID;

public class Channel extends Common{
    private String name;
    private final UUID userId;

    public Channel(String name, UUID userId) {
        super();
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void updateName(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
            super.updateUpdatedAt();
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + getName() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
