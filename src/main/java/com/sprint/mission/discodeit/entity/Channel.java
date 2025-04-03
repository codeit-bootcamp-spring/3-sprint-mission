package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private long createdAt;
    private long updatedAt;

    private String name;
    private List<User> attendees;
    private List<Message> messages;

    public Channel(String name, List<User> attendees) {
        this.name = name;
        this.attendees = attendees;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public List<User> getAttendees() {
        return attendees;
    }

    public List<Message> getMessages() {
        return messages;
    }


    // 필드를 수정하는 update 함수를 정의하세요.
    public void update() {
        // TODO: need to update this.updatedAt
    }
}
