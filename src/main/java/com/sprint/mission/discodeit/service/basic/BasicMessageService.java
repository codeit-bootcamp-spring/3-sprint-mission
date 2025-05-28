package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(MessageRequestDTO messageRequestDTO,
      List<BinaryContentDTO> binaryContentDTOS) {
    User user = findUser(messageRequestDTO.authorId());
    Channel channel = findChannel(messageRequestDTO.channelId());

    // Repository 저장용 데이터
    List<BinaryContent> binaryContents = convertBinaryContentDTOS(binaryContentDTOS);

    Message message = MessageRequestDTO.toEntity(messageRequestDTO);
    message.updateChannel(channel);
    message.updateAttachmentIds(binaryContents);

    userRepository.save(user);
    
    // 메시지를 보낸 channel의 mesagesList에 해당 메시지 추가
    channel.getMessages().add(message);
    channelRepository.save(channel);

    binaryContentRepository.saveAll(binaryContents);
    messageRepository.save(message);

    return message;
  }

  @Override
  public MessageResponseDTO findById(UUID messageId) {
    Message message = findMessage(messageId);

    return Message.toDTO(message);
  }

  @Override
  public List<MessageResponseDTO> findAllByChannelId(UUID channelId) {

    return messageRepository.findAllByChannelId(channelId).stream()
        .map(Message::toDTO)
        .toList();
  }

  @Override
  public MessageResponseDTO updateContent(UUID messageId, String content) {
    Message message = findMessage(messageId);

    message.updateContent(content);

    messageRepository.save(message);

    return Message.toDTO(message);
  }

  @Override
  public void deleteById(UUID messageId) {
    Message message = findMessage(messageId);

    // Channel의 메시지 목록에서 삭제
    Channel channel = message.getChannel();
    if (channel != null) {
      channel.getMessages().remove(message);
      channelRepository.save(channel);
    }

    for (BinaryContent binaryContent : message.getAttachmentIds()) {
      binaryContentRepository.deleteById(binaryContent.getId());
    }

    messageRepository.deleteById(messageId);
  }

  private List<BinaryContent> convertBinaryContentDTOS(List<BinaryContentDTO> binaryContentDTOS) {
    return binaryContentDTOS.stream()
        .map(BinaryContentDTO::toEntity)
        .toList();
  }

  private Message findMessage(UUID id) {
    return messageRepository.findById(id)
        .orElseThrow(NotFoundMessageException::new);
  }

  private Channel findChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(NotFoundChannelException::new);
  }

  private User findUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(NotFoundUserException::new);
  }
}
