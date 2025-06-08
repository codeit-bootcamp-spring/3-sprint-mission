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
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  //
  private final BinaryContentStorage binaryContentStorage;


  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + messageCreateRequest.channelId() + " not found"));

    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> new NoSuchElementException(
            "User with id " + messageCreateRequest.authorId() + " not found"));

    Message message = new Message(messageCreateRequest.content(), channel, author);

    for (BinaryContentCreateRequest request : binaryContentCreateRequests) {
      BinaryContent binaryContent = new BinaryContent(
          request.fileName(),
          (long) request.bytes().length,
          request.contentType()
      );

      binaryContentStorage.put(binaryContent.getId(), request.bytes());

      message.getAttachments().add(binaryContent);
    }

    Message saved = messageRepository.save(message);
    return messageMapper.toDto(saved);

  }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    return messageMapper.toDto(message);
  }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .map(messageMapper::toDto)
        .toList();
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    // 유효성
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    // Update
    message.update(request.newContent());

    return messageMapper.toDto(message);
  }

  @Override
  public void delete(UUID messageId) {
    // 유효성
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    messageRepository.delete(message);
  }

  // 페이징
  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public PageResponse<MessageDto> getMessages(UUID channelId, Instant before, int size) {

    Instant cursor = before != null ? before : Instant.now();

    Pageable pageable = PageRequest.of(0, size + 1);

    List<Message> messages = messageRepository
        .findByChannelIdAndCreatedBeforeOrderByCreatedAtDesc(channelId, cursor, pageable);

    boolean hasNext = messages.size() > size;

    if (hasNext) {
      messages = messages.subList(0, size);
    }

    List<MessageDto> content = messages.stream()
        .map(messageMapper::toDto)
        .toList();

    // 다음 조회 시 사용할 커서 결정
    // 마지막 메세지의 생성 시각을 다음 커서로 사용, 없다면 현재 커서 유지
    Instant nextCursor = hasNext ? messages.get(messages.size() - 1).getCreatedAt() : cursor;

    return new PageResponse<>(
        content,
        nextCursor,
        size,
        hasNext,
        // 총 메시지가 몇개인지 알 필요는 없습니다.
        null
    );
  }
}