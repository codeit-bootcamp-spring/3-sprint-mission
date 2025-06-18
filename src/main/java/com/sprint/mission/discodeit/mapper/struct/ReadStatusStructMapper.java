package com.sprint.mission.discodeit.mapper.struct;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusStructMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "channelId", source = "channel.id")
    ReadStatusResponseDto toDto(ReadStatus readStatus);
}
