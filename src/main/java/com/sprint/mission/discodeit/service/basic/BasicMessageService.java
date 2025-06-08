package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.utils.PageableBuilder;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;

  @Override
  public MessageDto create(MessageCreateRequest createRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) throws IllegalAccessException {
    User user = userRepository.findById(createRequest.authorId())
        .orElseThrow(() -> new NoSuchElementException(
            "Author with id " + createRequest.authorId() + " does not exist"));

    Channel channel = channelRepository.findById(createRequest.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + createRequest.channelId() + " does not exist"));

    /* 유저가 해당 채널에 있는지 validation check */
    List<UUID> participantIds = this.readStatusRepository.findAllByChannelId(
        createRequest.channelId()).stream().map(readStatus -> {
      return readStatus.getUser().getId();
    }).toList();

    if (!participantIds.contains(createRequest.authorId())) {
      throw new IllegalAccessException("유저가 참여하지 않은 채팅방에는 메세지를 보낼수 없습니다.");
    }

    Message message = new Message(user, channel, createRequest.content());

    /* 첨부 파일 생성, 선택적으로 여러개의 첨부파일 같이 등록 가능 */
    System.out.println("11111111 binaryContentCreateRequests = " + binaryContentCreateRequests);
    if (binaryContentCreateRequests != null) {
      binaryContentCreateRequests
          .forEach((binaryContentCreateRequest -> {
            /* 첨부 파일 생성 */
            BinaryContent binaryContent = binaryContentService.create(binaryContentCreateRequest);
            System.out.println("1111111binaryContent = " + binaryContent);
            /* message_attachments 테이블에 데이터 insert */
            message.getAttachments().add(binaryContent);
          }));
    }
    System.out.println("111111message.getAttachments() = " + message.getAttachments());
    Message createdMessage = this.messageRepository.save(message);

    /* 채널 lastMessageAt 업데이트 */
    channel.setLastMessageAt(Instant.now());
    this.channelRepository.save(channel);

    return messageMapper.toDto(createdMessage);
  }

  @Override
  public MessageDto findById(UUID messageId) {
    return this.messageRepository
        .findById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

  }

  @Override
  public Page<MessageDto> findAllByChannelId(UUID channelId, int page, int size,
      List<String> sorts) {

    Pageable pageable = PageableBuilder.builder(page, size, sorts.get(0), sorts.get(1));

    return this.messageRepository.findAllByChannelId(channelId,
            pageable)
        .map(messageMapper::toDto);
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest updateRequest) {
    Message message = this.messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.update(updateRequest.newContent(), Optional.empty());

    /* 업데이트 후 다시 DB 저장 */
    this.messageRepository.save(message);

    return this.messageRepository.findById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

  }

  @Override
  public void delete(UUID messageId) {
    // Message 객체 사용해야하므로 가져옴
    Message message = this.messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with messageId " + messageId + " not found"));

    if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
      for (BinaryContent binaryContent : message.getAttachments()) {
        this.binaryContentService.delete(binaryContent.getId());
      }
    }

    this.messageRepository.deleteById(messageId);
  }

}
