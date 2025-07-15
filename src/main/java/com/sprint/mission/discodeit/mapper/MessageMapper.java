package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper.advanced
 * FileName     : AdvancedMessageMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {

    @Mapping(source = "channel.id", target = "channelId")
    MessageResponse toDto(Message message);
}
