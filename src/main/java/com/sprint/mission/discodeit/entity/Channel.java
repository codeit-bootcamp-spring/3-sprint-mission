package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends Common{
    private String name;
    private List<User> members = new ArrayList<User>();

    public Channel(String name, User user) {
        super();
        this.name = name;
        this.members.add(user);
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void updateName(String name){
        this.name = name;
        super.updateUpdatedAt();
    }

    public void updateMembers(List<User> members) {
        this.members = members;
        super.updateUpdatedAt();
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
