package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Message 생성 및 수정 정보")
public record MessageRequestDTO(String content, UUID authorId, UUID channelId,
                                List<UUID> attachmentIds) {

  public static Message toEntity(MessageRequestDTO messageRequestDTO) {
    Message message = new Message(messageRequestDTO.authorId(), messageRequestDTO.channelId(),
        messageRequestDTO.content(),
        messageRequestDTO.attachmentIds);

    return message;
  }
}
