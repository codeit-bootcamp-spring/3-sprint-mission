package com.sprint.mission.discodeit.assembler;

import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageAssembler {

  private final UserMapper userMapper;
  private final UserOnlineService userOnlineService;
  private final BinaryContentMapper binaryContentMapper;

  public MessageResponse toResponse(Message message) {
    var author = message.getAuthor();

    boolean isOnline = userOnlineService.isOnline(author.getId());

    var base = userMapper.toResponse(author);

    var fullAuthor = new UserResponse(
        base.id(),
        base.username(),
        base.email(),
        base.profile(),
        isOnline,
        base.role()
    );

    var attachments = message.getAttachments().stream()
        .map(att -> binaryContentMapper.toResponse(att.getAttachment()))
        .toList();

    return new MessageResponse(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        fullAuthor,
        attachments
    );
  }
}
