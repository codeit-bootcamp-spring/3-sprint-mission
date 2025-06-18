package com.sprint.mission.discodeit.mapper.advanced;

import com.sprint.mission.discodeit.dto.readStatus.JpaReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper.advanced
 * FileName     : AdvancedReadStatusMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */
//@Component
@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id", target = "channelId")
    JpaReadStatusResponse toDto(ReadStatus readStatus);
}
