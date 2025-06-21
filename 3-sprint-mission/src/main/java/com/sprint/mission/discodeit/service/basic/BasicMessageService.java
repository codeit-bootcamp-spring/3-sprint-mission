package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDTO;
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
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public MessageDTO create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID userId = messageCreateRequest.authorId();
    UUID channelId = messageCreateRequest.channelId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

    String content = messageCreateRequest.content();
    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(contents -> {
          String fileName = contents.fileName();
          byte[] bytes = contents.bytes();
          String contentType = contents.contentType();

          BinaryContent binaryContent =
              BinaryContent.builder()
                  .fileName(fileName)
                  .size((long) bytes.length)
                  .contentType(contentType)
                  .build();

          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;

        })
        .toList();

    Message message =
        Message.builder()
            .currentChannel(channel)
            .currentUser(user)
            .content(content)
            .attachments(attachments)
            .build();

    messageRepository.save(message);
    return messageMapper.toDTO(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDTO> findAllByChannelId(
      UUID channelId,
      Instant createdAt,
      Pageable pageable
  ) {
    Slice<MessageDTO> slice = messageRepository.findAllByChannelId(
        channelId,
        Optional.ofNullable(createdAt).orElse(Instant.now()),
        pageable
    ).map(messageMapper::toDTO);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }

    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDTO find(UUID id) {

    return messageRepository.findById(id)
        .map(messageMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
  }


  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDTO> findAllByChannelIdAndContent(
      UUID channelId,
      String content,
      Instant createdAt,
      Pageable pageable
  ) {
    Slice<MessageDTO> slice = messageRepository.findAllByChannelIdAndContent(
        channelId,
        content,
        Optional.ofNullable(createdAt).orElse(Instant.now()),
        pageable
    ).map(messageMapper::toDTO);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }

    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Override
  public MessageDTO update(UUID id, MessageUpdateRequest messageUpdateDTO) {
    String newContent = messageUpdateDTO.newContent();
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
    message.update(newContent);

    return messageMapper.toDTO(message);
  }

  @Override
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
    message.getAttachments().stream()
        .map(BinaryContent::getId)
        .forEach(binaryContentRepository::deleteById);

    messageRepository.deleteById(id);

  }
}
