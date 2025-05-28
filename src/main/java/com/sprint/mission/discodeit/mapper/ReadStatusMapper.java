package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusResponseDto toDto(ReadStatus readStatus) {
    return new ReadStatusResponseDto(readStatus.getId(), readStatus.getUser().getId(),
        readStatus.getChannel().getId(), readStatus.getLastReadAt());
  }
}
