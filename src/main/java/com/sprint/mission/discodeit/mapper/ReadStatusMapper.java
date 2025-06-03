package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  @Mapping(target = "userId", expression = "java(readStatus.getUser() != null ? readStatus.getUser().getId() : null)")
  @Mapping(target = "channelId", expression = "java(readStatus.getChannel() != null ? readStatus.getChannel().getId() : null)")
  ReadStatusDto toDto(ReadStatus readStatus);
}
