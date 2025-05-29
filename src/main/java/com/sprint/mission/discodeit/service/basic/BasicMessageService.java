package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.MessageException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public MessageResponse create(CreateMessageCommand command) {
    User author = userRepository.findById(command.authorId())
        .orElseThrow(() -> UserException.notFound(command.authorId()));
    Channel channel = channelRepository.findById(command.channelId())
        .orElseThrow(() -> ChannelException.notFound(command.channelId()));
    Message message = Message.create(command.content(), author, channel);

    command.attachments().forEach(attachmentRequest -> {
      BinaryContent binaryContent = BinaryContent.create(
          attachmentRequest.fileName(),
          (long) attachmentRequest.bytes().length,
          attachmentRequest.contentType(),
          attachmentRequest.bytes()
      );
      BinaryContent saved = binaryContentRepository.save(binaryContent);
      message.attach(saved);
    });

    return MessageResponse.from(messageRepository.save(message));
  }

  @Override
  public MessageResponse findById(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(MessageResponse::from)
        .orElseThrow(() -> MessageException.notFound(messageId));
  }

  @Override
  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .map(MessageResponse::from)
        .toList();
  }

  @Override
  public MessageResponse updateContent(UUID messageId, String newContent) {
    return messageRepository.findById(messageId)
        .map(message -> {
          message.updateContent(newContent);
          return MessageResponse.from(messageRepository.save(message));
        })
        .orElseThrow(() -> MessageException.notFound(messageId));
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> MessageException.notFound(messageId));

    for (var attachment : message.getAttachments()) {
      System.out.println("attachment = " + attachment);
      System.out.println("binaryContent = " + attachment.getBinaryContent());
      System.out.println("binaryContentId = " +
          (attachment.getBinaryContent() != null ? attachment.getBinaryContent().getId() : "null")
      );
    }

    messageRepository.deleteById(messageId);
  }
}
