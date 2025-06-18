package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.mapper.EntityDtoMapper;
import com.sprint.mission.discodeit.dto.mapper.PageMapper;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final EntityDtoMapper entityDtoMapper;
  private final PageMapper pageMapper;
  private static final Logger log = LoggerFactory.getLogger(BasicMessageService.class);

  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    log.info("메시지 생성 요청 - 채널ID: {}, 작성자ID: {}, 내용: {}", channelId, authorId, messageCreateRequest.content());

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new CustomException.ChannelNotFoundException("Channel with id " + channelId + " does not exist"));

    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("Author with id " + authorId + " does not exist"));

    log.info("채널과 사용자 조회 완료 - 채널: {}, 사용자: {}", channel.getName(), author.getUsername());

    // 첨부파일이 있는 경우 - BinaryContent 생성 후 Storage에 저장
    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes = attachmentRequest.bytes();

          // 1. 메타정보만으로 BinaryContent 생성 및 저장
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
          BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

          // 2. 실제 바이너리 데이터는 Storage에 저장
          binaryContentStorage.put(savedBinaryContent.getId(), bytes);

          return savedBinaryContent;
        })
        .collect(Collectors.toList());

    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author, attachments);

    Message savedMessage = messageRepository.save(message);
    log.info("메시지 저장 완료 - ID: {}, 내용: {}", savedMessage.getId(), savedMessage.getContent());

    return entityDtoMapper.toDto(savedMessage);
  }

  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    log.info("메시지 생성 요청 (첨부파일 없음) - 채널ID: {}, 작성자ID: {}, 내용: {}", channelId, authorId, messageCreateRequest.content());

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new CustomException.ChannelNotFoundException("Channel with id " + channelId + " does not exist"));

    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("Author with id " + authorId + " does not exist"));

    log.info("채널과 사용자 조회 완료 - 채널: {}, 사용자: {}", channel.getName(), author.getUsername());

    // 첨부파일이 없는 경우
    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author);

    Message savedMessage = messageRepository.save(message);
    log.info("메시지 저장 완료 - ID: {}, 내용: {}", savedMessage.getId(), savedMessage.getContent());

    return entityDtoMapper.toDto(savedMessage);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(entityDtoMapper::toDto)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    log.info("메시지 조회 요청 - 채널ID: {}", channelId);
    // N+1 문제 해결: Fetch Join으로 작성자 정보를 한 번에 조회
    List<Message> messages = messageRepository.findAllByChannelIdWithAuthorOrderByCreatedAtAsc(channelId);
    log.info("메시지 조회 완료 - 채널ID: {}, 메시지 개수: {}", channelId, messages.size());
    return entityDtoMapper.toMessageDtoList(messages);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelIdWithPaging(UUID channelId, Pageable pageable) {
    log.info("메시지 페이징 조회 요청 - 채널ID: {}, 페이지: {}, 크기: {}", channelId, pageable.getPageNumber(), pageable.getPageSize());
    // N+1 문제 해결: Fetch Join으로 작성자 정보를 한 번에 조회
    Page<Message> messagePage = messageRepository.findAllByChannelIdWithAuthor(channelId, pageable);
    log.info("메시지 페이징 조회 완료 - 채널ID: {}, 총 메시지: {}, 현재 페이지: {}", channelId, messagePage.getTotalElements(),
        messagePage.getNumber());
    return pageMapper.toPageResponse(messagePage, entityDtoMapper::toDto);
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();

    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));

    message.update(newContent);

    return entityDtoMapper.toDto(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));

    messageRepository.delete(message);
  }
}
