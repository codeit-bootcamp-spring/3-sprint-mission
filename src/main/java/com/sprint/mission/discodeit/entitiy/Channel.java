package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private ChannelType type;
    private String channelName;
    private String description;

    public Channel(String channelName, ChannelType type, String description) {
        this.channelName = channelName;
        this.description = description;
        this.type = type;
        this.id = UUID.randomUUID();
        this.createdAt =Instant.now();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                ", channelName='" + channelName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
