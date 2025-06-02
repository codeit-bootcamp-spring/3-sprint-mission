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
import java.util.Collections;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
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
  @Transactional(Transactional.TxType.SUPPORTS)
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
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
  public PageResponse<MessageDto> getMessages(UUID channelId, int page, int size) {

    // 초기값
    int fromIndex = page * size;
    int toIndex = fromIndex + size + 1;

    List<Message> allMessages = messageRepository.findAllByChannelId(channelId);

    // 생성일자 기준 내림차순 정렬
    allMessages.sort(Comparator.comparing(Message::getCreatedAt).reversed());

    // 페이징 + 다음 페이지 여부 확인
    List<Message> pagedMessages;
    boolean hasNext = false;

    if (fromIndex >= allMessages.size()) {
      pagedMessages = Collections.emptyList();
    } else {
      int actualToIndex = Math.min(toIndex, allMessages.size());
      pagedMessages = allMessages.subList(fromIndex, actualToIndex);
      hasNext = pagedMessages.size() > size;
      if (hasNext) {
        pagedMessages = pagedMessages.subList(0, size);
      }
    }

    List<MessageDto> content = pagedMessages.stream()
        .map(messageMapper::toDto)
        .toList();

    return new PageResponse<>(
        content,
        page,
        size,
        hasNext,
        // 총 메시지가 몇개인지 알 필요는 없습니다.
        null
    );
  }
}