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
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(
      MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests
  ) {
    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + messageCreateRequest.channelId() + " does not exist"));
    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> new NoSuchElementException(
            "Author with id " + messageCreateRequest.authorId() + " does not exist"));

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(binaryContentCreateRequest -> {
          BinaryContent binaryContent = new BinaryContent(
              binaryContentCreateRequest.fileName(),
              (long) binaryContentCreateRequest.bytes().length,
              binaryContentCreateRequest.contentType()
          );

          BinaryContent saved = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(saved.getId(), binaryContentCreateRequest.bytes());
          return saved;
        })
        .toList();

    String content = messageCreateRequest.content();
    Message message = new Message(
        content,
        channel,
        author,
        attachments
    );
    messageRepository.save(message);

    return messageMapper.toDto(message);
  }

  @Override
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
    int size = pageable.getPageSize();
    Pageable cursorPageable = PageRequest.of(0,size + 1, Sort.by("createdAt").descending());

    Slice<Message> slice;
    if (cursor == null) {
      slice = messageRepository.findAllByChannelId(channelId, cursorPageable);
    } else {
      slice = messageRepository.findAllByChannelId(channelId, cursor, cursorPageable);
    }

    List<MessageDto> messageDtos = slice
        .map(messageMapper::toDto)
        .getContent();

    //hasNext 계산
    boolean hasNext = messageDtos.size() > size;
    List<MessageDto> content = hasNext ? messageDtos.subList(0, size) : messageDtos;

    //nextCursor 계산
    Instant nextCursor = hasNext ? content.get(content.size() - 1).createdAt() : null;

    Slice<MessageDto> dtoSlice = new SliceImpl<>(content, pageable, hasNext);

    return pageResponseMapper.fromSlice(dtoSlice, nextCursor);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(newContent);
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    binaryContentRepository.deleteAll(message.getAttachments());
    messageRepository.delete(message);
  }
}
