package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {
        BinaryContentMapper.class,
        UserMapper.class,
        MessageAttachmentMapper.class
    }
)
public interface MessageMapper {

  @Mapping(target = "channelId", source = "channel.id")
  @Mapping(target = "attachments", source = "attachments")
  MessageResponse toResponse(Message message);

  List<MessageResponse> fromEntityList(List<Message> messages);
}