package com.sprint.mission.discodeit.entity;


public class User extends Common{
    private String name;

    public User(String name) {
        super();
        this.name = name;
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
        return "User{" +
                "name='" + getName() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
