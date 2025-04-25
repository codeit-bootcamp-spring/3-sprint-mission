package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.file.FileUserService.UserNotFoundException;
import com.sprint.mission.discodeit.service.file.FileUserService.UserNotParticipantException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileMessageService implements MessageService {

  private final FileMessageRepository messageRepository;
  private final FileUserRepository userRepository;
  private final FileChannelRepository channelRepository;

  public FileMessageService(FileMessageRepository messageRepository,
      FileUserRepository userRepository,
      FileChannelRepository channelRepository) {
    this.messageRepository = messageRepository;
    this.userRepository = userRepository;
    this.channelRepository = channelRepository;
  }

  public static FileMessageService from(FileUserRepository userRepository,
      FileChannelRepository channelRepository,
      String filePath) {
    return new FileMessageService(FileMessageRepository.from(filePath), userRepository,
        channelRepository);
  }

  public static FileMessageService createDefault(FileUserRepository userRepository,
      FileChannelRepository channelRepository) {
    return new FileMessageService(FileMessageRepository.createDefault(), userRepository,
        channelRepository);
  }

  @Override
  public Message createMessage(String content, UUID userId, UUID channelId)
      throws ChannelException {
    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));

    boolean isNotParticipant = channel.getParticipants().stream()
        .noneMatch(p -> p.getId().equals(userId));

    if (isNotParticipant) {
      throw new UserNotParticipantException();
    }

    Message message = Message.create(content, userId, channelId);
    return messageRepository.save(message);
  }

  @Override
  public Optional<Message> getMessageById(UUID id) {
    return messageRepository.findById(id);
  }

  @Override
  public List<Message> searchMessages(UUID channelId, UUID userId, String content)
      throws ChannelException, UserException {
    // 채널/사용자 존재 여부 검증 (필요 시)
    if (channelId != null) {
      channelRepository.findById(channelId)
          .orElseThrow(() -> ChannelException.notFound(channelId));
    }
    if (userId != null) {
      userRepository.findById(userId)
          .orElseThrow(() -> UserException.notFound(userId));
    }

    return messageRepository.findAll().stream()
        .filter(m ->
            (channelId == null || m.getChannelId().equals(channelId)) &&
                (userId == null || m.getUserId().equals(userId)) &&
                (content == null || m.getContent().contains(content)))
        .toList();
  }

  @Override
  public List<Message> getChannelMessages(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public Optional<Message> updateMessageContent(UUID id, String content) {
    return messageRepository.findById(id)
        .map(message -> {
          message.updateContent(content);
          return messageRepository.save(message);
        });
  }

  @Override
  public Optional<Message> deleteMessage(UUID id) {
    Optional<Message> message = messageRepository.findById(id);
    message.ifPresent(m -> messageRepository.deleteById(id));
    return message;
  }
}
