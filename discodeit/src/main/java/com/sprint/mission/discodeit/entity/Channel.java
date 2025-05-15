package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private ChannelType type;
    private String channelName;
    private String description;
    private List<UUID> paricipantIds;

    public Channel(ChannelType type, String channelName, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.type = type;
        this.channelName = channelName;
        this.description = description;
        paricipantIds = new ArrayList<>();
    }


    public void update(String newName, String newDescription) {
        if (newName != null && !newName.equals(this.channelName)) {
            this.channelName = newName;
            this.updatedAt = Instant.now();
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            this.updatedAt = Instant.now();
        }

    }

    public void addParicipant(UUID paricipantId){
        paricipantIds.add(paricipantId);
        this.updatedAt = Instant.now();
    }

    public void deleteParicipant(UUID paricipantId){
        paricipantIds.remove(paricipantId);
        this.updatedAt = Instant.now();
    }
}
