package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String channelName;
    private UUID channelMaster;
    private String description;
    private boolean isPrivate;
    private final List<UUID> users;
    private final List<UUID> messages;
    private Instant lastMessageTime;

    public Channel(String channelName, UUID channelMaster, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.channelName = channelName;
        this.channelMaster = channelMaster;
        this.description = description;
        this.isPrivate = false;
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public Channel(UUID channelMaster, List<UUID> users) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.channelName = "";
        this.channelMaster = channelMaster;
        this.description = "";
        this.isPrivate = true;
        this.users = users;
        this.messages = new ArrayList<>();
    }
    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = Instant.now();
    }

    public void updateChannelMaster(UUID userId) {
        this.channelMaster = userId;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void updateLastMessageTime(Instant lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public static ChannelResponseDTO toDTO(Channel channel) {
        ChannelResponseDTO channelResponseDTO = new ChannelResponseDTO(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getChannelName(),
                channel.getChannelMaster(),
                channel.getDescription(),
                channel.isPrivate(),
                channel.getUsers(),
                channel.getMessages(),
                channel.getLastMessageTime()
        );

        return channelResponseDTO;
    }

    @Override
    public String toString() {
        return "Channel {\n" +
                "  id=" + id + ",\n" +
                "  createdAt=" + createdAt + ",\n" +
                "  updatedAt=" + updatedAt + ",\n" +
                "  channelName='" + channelName + "',\n" +
                "  channelMaster='" + channelMaster + "', \n" +
                "  description='" + description + "',\n" +
                "  users=" + users.stream().toList() + ",\n" +
                "  messages=" + messages.stream().toList() + "\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(id, channel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
