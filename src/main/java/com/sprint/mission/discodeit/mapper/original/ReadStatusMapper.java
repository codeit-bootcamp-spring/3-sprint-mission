package com.sprint.mission.discodeit.mapper.original;

import com.sprint.mission.discodeit.dto.readStatus.JpaReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : ReadStatusMapper
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Component
public class ReadStatusMapper {
    public JpaReadStatusResponse toDto(ReadStatus readStatus) {
        if(readStatus == null) return null;

        return JpaReadStatusResponse.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUser().getId())
                .channelId(readStatus.getChannel().getId())
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }
}
