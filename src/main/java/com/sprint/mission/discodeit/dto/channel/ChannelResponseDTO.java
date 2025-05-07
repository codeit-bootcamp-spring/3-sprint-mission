package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ChannelResponseDTO {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String channelName;
    private UUID channelMaster;
    private String description;
    private boolean isPrivate;
    private List<UUID> users;
    private List<UUID> messages;
    private Instant lastMessageTime;

    public ChannelResponseDTO() {
    }

    public static ChannelResponseDTO toDTO(Channel channel) {
        ChannelResponseDTO channelResponseDTO = new ChannelResponseDTO();

        channelResponseDTO.setId(channel.getId());
        channelResponseDTO.setCreatedAt(channel.getCreatedAt());
        channelResponseDTO.setUpdatedAt(channel.getUpdatedAt());
        channelResponseDTO.setChannelName(channel.getChannelName());
        channelResponseDTO.setChannelMaster(channel.getChannelMaster());
        channelResponseDTO.setDescription(channel.getDescription());
        channelResponseDTO.setPrivate(channel.isPrivate());
        channelResponseDTO.setUsers(channel.getUsers());
        channelResponseDTO.setMessages(channel.getMessages());

        return channelResponseDTO;
    }

    @Override
    public String toString() {
        return "ChannelResponseDTO{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", channelMaster=" + channelMaster +
                ", description='" + description + '\'' +
                ", isPrivate=" + isPrivate +
                ", users=" + users +
                ", messages=" + messages +
                ", lastMessageTime=" + lastMessageTime +
                '}';
    }
}
