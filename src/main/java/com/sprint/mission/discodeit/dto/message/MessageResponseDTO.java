package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MessageResponseDTO {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private UUID senderId;
    private UUID channelId;
    private List<UUID> attachmentIds;

    public MessageResponseDTO() {
    }

    public static MessageResponseDTO toDTO(Message message) {
        MessageResponseDTO messageResponseDTO = new MessageResponseDTO();

        messageResponseDTO.setId(message.getId());
        messageResponseDTO.setCreatedAt(message.getCreatedAt());
        messageResponseDTO.setUpdatedAt(message.getUpdatedAt());
        messageResponseDTO.setContent(message.getContent());
        messageResponseDTO.setSenderId(message.getSenderId());
        messageResponseDTO.setChannelId(message.getChannelId());
        messageResponseDTO.setAttachmentIds(message.getAttachmentIds());

        return messageResponseDTO;
    }

    @Override
    public String toString() {
        return "MessageResponseDTO{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", senderId=" + senderId +
                ", channelId=" + channelId +
                ", attachmentIds=" + attachmentIds +
                '}';
    }
}
