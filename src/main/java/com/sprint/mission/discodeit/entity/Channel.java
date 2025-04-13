package com.sprint.mission.discodeit.entity;

import java.util.*;
import java.util.UUID;

public class Channel extends Base {
    private String channelName;
    private List<User> members = new ArrayList<>(); // Channel List안에 members List를 넣고 member와 message로 이루어진 LinkedList를 만들고 싶음
    private UUID channelId;

    public Channel(String channelName, User user) {
        super();
        this.channelName = channelName;
        this.members.add(user);
    }


    // Gettter Method

    public String getChannelName() {
        return this.channelName;
    }

    public List<User> getMembers() {
        return this.members;
    }


    // Update Method

    public void updateChannelId(UUID channelId) {
        this.channelId = super.getId();
        super.updateUpdatedAt();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        super.updateUpdatedAt();
    }

    public void updateMembers(List<User> members) {
        this.members = new ArrayList<>(members);
        super.updateUpdatedAt();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + getChannelName() + '\'' +
                ", channelId='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}