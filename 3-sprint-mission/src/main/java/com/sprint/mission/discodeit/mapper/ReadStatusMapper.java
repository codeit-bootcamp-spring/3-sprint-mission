package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusDTO toDTO(ReadStatus readStatus);
}
