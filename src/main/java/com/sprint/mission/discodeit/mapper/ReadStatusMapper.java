// com.sprint.mission.discodeit.mapper.ReadStatusMapper.java
package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStatusMapper {

    public ReadStatusDto toDto(ReadStatus readStatus) {
        return ReadStatusDto.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUser().getId())
                .channelId(readStatus.getChannel().getId())
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }
}