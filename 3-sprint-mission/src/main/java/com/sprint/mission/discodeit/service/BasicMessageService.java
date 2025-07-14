package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID userId = messageCreateRequest.authorId();
    UUID channelId = messageCreateRequest.channelId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("해당 채널이 존재하지 않습니다. channelId={}", channelId);
          return new ChannelNotFoundException(channelId);
        });

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("해당 사용자가 존재하지 않습니다. userId={}", userId);
          return UserNotFoundException.fromUserId(userId);
        });

    String content = messageCreateRequest.content();

    log.debug("메시지 첨부 파일 객체 생성 시작 request={}", binaryContentCreateRequests);
    int total = binaryContentCreateRequests.size();
    List<BinaryContent> attachments = IntStream.range(0, total)
        .mapToObj(i -> {
          BinaryContentCreateRequest contents = binaryContentCreateRequests.get(i);
          String fileName = contents.fileName();
          byte[] bytes = contents.bytes();
          String contentType = contents.contentType();

          log.debug("[{} / {}] 메시지 첨부 파일 객체 생성 시작", i + 1, total);
          BinaryContent binaryContent =
              BinaryContent.builder()
                  .fileName(fileName)
                  .size((long) bytes.length)
                  .contentType(contentType)
                  .build();
          log.debug("[{} / {}] 메시지 첨부 파일 생성 완료 binaryContents={}", i + 1, total, binaryContent);

          binaryContentRepository.save(binaryContent);
          log.debug("[{} / {}][binaryContentRepository] 메시지 첨부 파일 저장 완료 {}", i + 1, total,
              binaryContent);

          binaryContentStorage.put(binaryContent.getId(), bytes);
          log.debug("[{} / {}][binaryContentStorage] 메시지 첨부 파일 저장 완료 {}", i + 1, total,
              binaryContent);

          return binaryContent;
        })
        .toList();

    log.debug("메시지 객체 생성 시작 request={}", messageCreateRequest);
    Message message =
        Message.builder()
            .currentChannel(channel)
            .currentUser(user)
            .content(content)
            .attachments(attachments)
            .build();
    log.debug("메시지 객체 생성 완료 messageId={}", message.getId());

    log.debug("[messageRepository] 메시지 저장 시작 messageId={}", message.getId());
    messageRepository.save(message);
    log.debug("[messageRepository] 메시지 저장 완료 messageId={}", message.getId());

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(
      UUID channelId,
      Instant createdAt,
      Pageable pageable
  ) {
    Slice<MessageDto> slice = messageRepository.findAllByChannelIdAndUser(
        channelId,
        Optional.ofNullable(createdAt).orElse(Instant.now()),
        pageable
    ).map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }

    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID id) {

    return messageRepository.findById(id)
        .map(messageMapper::toDto)
        .orElseThrow(() -> new MessageNotFoundException(id));
  }


  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelIdAndContent(
      UUID channelId,
      String content,
      Instant createdAt,
      Pageable pageable
  ) {
    Slice<MessageDto> slice = messageRepository.findAllByChannelIdAndContent(
        channelId,
        content,
        Optional.ofNullable(createdAt).orElse(Instant.now()),
        pageable
    ).map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }

    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Override
  @Transactional
  public MessageDto update(UUID id, MessageUpdateRequest messageUpdateDto) {
    String newContent = messageUpdateDto.newContent();
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("해당 메시지가 존재하지 않습니다. messageId={}", id);
          return new MessageNotFoundException(id);
        });

    log.debug("[messageRepository] 메시지 업데이트 시작 messageId={}", id);
    message.update(newContent);
    log.debug("[messageRepository] 메시지 업데이트 완료 messageId={}", id);

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("해당 메시지가 존재하지 않습니다. messageId={}", id);
          return new MessageNotFoundException(id);
        });

    log.debug("[binaryContentRepository] 메시지 첨부 파일 삭제 시작 messageId={}", message.getId());
    message.getAttachments().stream()
        .map(BinaryContent::getId)
        .forEach(binaryContentRepository::deleteById);
    log.debug("[binaryContentRepository] 메시지 첨부 파일 삭제 완료 messageId={}", message.getId());

    log.debug("[messageRepository] 메시지 삭제 시작 messageId={}", message.getId());
    messageRepository.deleteById(id);
    log.debug("[messageRepository] 메시지 삭제 완료 messageId={}", message.getId());

  }
}
