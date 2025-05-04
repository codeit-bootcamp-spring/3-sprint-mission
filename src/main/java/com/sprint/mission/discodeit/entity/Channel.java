package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private ChannelType type;
    private String name;
    private String description;
    //
    private ReadStatus readStatus;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        //
        this.type = type;
        this.name = name;
        this.description = description;
        //
        this.readStatus = null; // 채널을 읽은 유저들의 상태
    }

//    public UUID getId() {
//        return id;
//    }
//
//    public Long getCreatedAt() {
//        return createdAt;
//    }
//
//    public Long getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public ChannelType getType() {
//        return type;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getDescription() {
//        return description;
//    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        }
    }
}
