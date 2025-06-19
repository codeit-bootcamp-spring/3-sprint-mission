package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class ReadStatusMapper {

    public ReadStatusDto toDto(ReadStatus readStatus) {
        UUID id = readStatus.getId();
        UUID userId = readStatus.getUser().getId();
        UUID channelId = readStatus.getChannel().getId();
        Instant lastReadAt = readStatus.getLastReadAt();

        return new ReadStatusDto(id, userId, channelId, lastReadAt);
    }
}
