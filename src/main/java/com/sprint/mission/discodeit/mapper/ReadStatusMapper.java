package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id", target = "channelId")
    public ReadStatusDto toDto(ReadStatus readStatus);
}
