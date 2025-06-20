package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Transactional
  @Override
  public MessageDto create(MessageCreateRequest createRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    User user = userRepository.findById(createRequest.authorId())
        .orElseThrow(() -> new NoSuchElementException(
            "Author with id " + createRequest.authorId() + " does not exist"));

    Channel channel = channelRepository.findById(createRequest.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + createRequest.channelId() + " does not exist"));

    /* 유저가 해당 채널에 있는지 validation check */
    if (!this.readStatusRepository.existsByUserIdAndChannelId(createRequest.authorId(),
        createRequest.channelId())) {
      throw new NoSuchElementException("유저가 참여하지 않은 채팅방에는 메세지를 보낼수 없습니다.");
    }

    /* 첨부 파일 생성, 선택적으로 여러개의 첨부파일 같이 등록 가능 */
    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes = attachmentRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .toList();

    Message message = new Message(user, channel, createRequest.content(), attachments);

    this.messageRepository.save(message);

    /* 채널 lastMessageAt 업데이트 */
    channel.setLastMessageAt(Instant.now());

    return messageMapper.toDto(message);
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
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createdAt,
      Pageable pageable) {

    Slice<MessageDto> slice = this.messageRepository.findAllByChannelIdWithAuthor(channelId,
        Optional.ofNullable(createdAt).orElse(Instant.now()), pageable).map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }
    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Transactional
  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest updateRequest) {
    Message message = this.messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(updateRequest.newContent());

    return messageMapper.toDto(message);
  }

  @Transactional
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
