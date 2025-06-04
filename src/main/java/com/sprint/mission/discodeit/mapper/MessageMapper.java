package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {

    @Mapping(target = "channelId", expression = "java(message.getChannel() != null ? message.getChannel().getId() : null)")
    MessageDto toDto(Message message);
}
