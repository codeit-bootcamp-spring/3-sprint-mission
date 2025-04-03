package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private long createdAt;
    private long updatedAt;

    private String name;
    private List<User> attendees;
    private List<Message> messages;

    // Q. attendees는 처음부터 리스트로 받아야하나? 아니면 User로 받고 생성자 안에서 처리해줘야하나?
    public Channel(String name, User attendee) {
        // for fixed unique id
        this.id = UUID.nameUUIDFromBytes(name.concat(attendee.getName()).getBytes());
        this.name = name;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = Instant.now().getEpochSecond();
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

    // Q.updateAttendees? or setAttendees?
    public void setAttendees(List<User> attendees) {
        this.attendees = attendees;
    }

    public List<Message> getMessages() {
        return messages;
    }

    // Q. list로 받는게 맞나??
    public void setMessages(List<Message> msg) {
        this.messages = messages;
    }

    // 필드를 수정하는 update 함수를 정의하세요.
    public void update(String name) {
        // TODO: add setter method for field
        this.name = name;
        this.updatedAt = Instant.now().getEpochSecond();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", attendees=" + attendees +
                ", messages=" + messages +
                '}';
    }
}
