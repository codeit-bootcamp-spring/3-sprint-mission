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
        
        // 프록시 객체 안전 접근
        UUID userId = null;
        try {
            userId = readStatus.getUser().getId();
        } catch (Exception e) {
            // 프록시 객체 접근 실패 시 null 처리
        }
        
        UUID channelId = null;
        try {
            channelId = readStatus.getChannel().getId();
        } catch (Exception e) {
            // 프록시 객체 접근 실패 시 null 처리
        }
        
        Instant lastReadAt = readStatus.getLastReadAt();

        return new ReadStatusDto(id, userId, channelId, lastReadAt);
    }
}
