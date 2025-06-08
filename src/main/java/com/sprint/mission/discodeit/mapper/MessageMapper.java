package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ChannelMapper.class,
    BinaryContentMapper.class})
public interface MessageMapper {

  @Mapping(target = "channelId", source = "message.channel.id")
  @Mapping(target = "author", source = "message.user")
  MessageDto toDto(Message message);

  Message messageDtoToMessage(MessageDto messageDto);
}
