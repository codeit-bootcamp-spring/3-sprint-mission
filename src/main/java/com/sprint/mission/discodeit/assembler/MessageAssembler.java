package com.sprint.mission.discodeit.assembler;

import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageAssembler {

  private final UserMapper userMapper;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentMapper binaryContentMapper;

  public MessageResponse toResponse(Message message) {
    var author = message.getAuthor();

    boolean isOnline = userStatusRepository.findByUserId(author.getId())
        .map(UserStatus::isOnline)
        .orElse(false);

    var base = userMapper.toResponse(author);

    var fullAuthor = new UserResponse(
        base.id(),
        base.username(),
        base.email(),
        base.profile(),
        isOnline
    );

    var attachments = message.getAttachments().stream()
        .map(att -> binaryContentMapper.toResponse(att.getBinaryContent()))
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
