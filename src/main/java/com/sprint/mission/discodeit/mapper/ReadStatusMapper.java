package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusDto toDto(ReadStatus entity) {
    return new ReadStatusDto(
        entity.getId(),
        entity.getUser().getId(),
        entity.getChannel().getId(),
        entity.getLastReadAt()
    );
  }

  public ReadStatusResponse toResponse(ReadStatus entity) {
    return new ReadStatusResponse(
        entity.getId(),
        entity.getUser().getId(),
        entity.getChannel().getId(),
        entity.getLastReadAt()
    );
  }
}
