package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatusResponseDTO {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadTime;

    public ReadStatusResponseDTO() {
    }

    public static ReadStatusResponseDTO toDTO(ReadStatus readStatus) {
        ReadStatusResponseDTO readStatusResponseDTO = new ReadStatusResponseDTO();

        readStatusResponseDTO.setId(readStatus.getId());
        readStatusResponseDTO.setCreatedAt(readStatus.getCreatedAt());
        readStatusResponseDTO.setUpdatedAt(readStatus.getUpdatedAt());
        readStatusResponseDTO.setUserId(readStatus.getUserId());
        readStatusResponseDTO.setChannelId(readStatus.getChannelId());
        readStatusResponseDTO.setLastReadTime(readStatus.getLastReadTime());

        return readStatusResponseDTO;
    }

    @Override
    public String toString() {
        return "ReadStatusResponseDTO{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", lastReadTime=" + lastReadTime +
                '}';
    }
}
