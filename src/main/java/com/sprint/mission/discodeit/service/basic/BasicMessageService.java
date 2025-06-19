package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.mapper.PageMapper;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;
  private final MapperFacade mapperFacade;
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

    // 🚀 개선: BinaryContentService에 위임하여 중복 제거
    List<BinaryContent> attachments = binaryContentService.createAll(binaryContentCreateRequests);

    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author, attachments);

    Message savedMessage = messageRepository.save(message);
    log.info("메시지 저장 완료 - ID: {}, 내용: {}", savedMessage.getId(), savedMessage.getContent());

    return mapperFacade.toDto(savedMessage);
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

    return mapperFacade.toDto(savedMessage);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(mapperFacade::toDto)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    log.info("메시지 조회 요청 - 채널ID: {}", channelId);
    // N+1 문제 해결: Fetch Join으로 작성자와 첨부파일 정보를 한 번에 조회
    List<Message> messages = messageRepository.findAllByChannelIdWithAuthorAndAttachmentsOrderByCreatedAtAsc(channelId);
    log.info("메시지 조회 완료 - 채널ID: {}, 메시지 개수: {}", channelId, messages.size());
    return mapperFacade.toMessageDtoList(messages);
  }

  /**
   * 성능 최적화를 위한 선택적 조회 메서드
   * 첨부파일이 필요 없는 경우 작성자 정보만 조회
   */
  @Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelIdWithAuthorOnly(UUID channelId) {
    log.info("메시지 조회 요청 (작성자만) - 채널ID: {}", channelId);
    // 작성자 정보만 필요한 경우 가벼운 쿼리 사용
    List<Message> messages = messageRepository.findAllByChannelIdWithAuthorOrderByCreatedAtAsc(channelId);
    log.info("메시지 조회 완료 (작성자만) - 채널ID: {}, 메시지 개수: {}", channelId, messages.size());
    return mapperFacade.toMessageDtoList(messages);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelIdWithPaging(UUID channelId, Pageable pageable) {
    log.info("메시지 페이징 조회 요청 - 채널ID: {}, 페이지: {}, 크기: {}", channelId, pageable.getPageNumber(), pageable.getPageSize());
    // N+1 문제 해결: Fetch Join으로 작성자와 첨부파일 정보를 한 번에 조회
    Page<Message> messagePage = messageRepository.findAllByChannelIdWithAuthorAndAttachments(channelId, pageable);
    log.info("메시지 페이징 조회 완료 - 채널ID: {}, 총 메시지: {}, 현재 페이지: {}", channelId, messagePage.getTotalElements(),
        messagePage.getNumber());
    return pageMapper.toPageResponse(messagePage, mapperFacade::toDto);
  }

  /**
   * 성능 최적화를 위한 선택적 페이징 조회 메서드
   * 첨부파일이 필요 없는 경우 작성자 정보만 조회
   */
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelIdWithPagingAuthorOnly(UUID channelId, Pageable pageable) {
    log.info("메시지 페이징 조회 요청 (작성자만) - 채널ID: {}, 페이지: {}, 크기: {}", channelId, pageable.getPageNumber(),
        pageable.getPageSize());
    // 작성자 정보만 필요한 경우 가벼운 쿼리 사용
    Page<Message> messagePage = messageRepository.findAllByChannelIdWithAuthor(channelId, pageable);
    log.info("메시지 페이징 조회 완료 (작성자만) - 채널ID: {}, 총 메시지: {}, 현재 페이지: {}", channelId, messagePage.getTotalElements(),
        messagePage.getNumber());
    return pageMapper.toPageResponse(messagePage, mapperFacade::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelIdWithCursorPaging(UUID channelId, String cursor, Pageable pageable) {
    log.info("메시지 커서 페이징 조회 요청 - 채널ID: {}, 커서: {}, 페이지 크기: {}", channelId, cursor, pageable.getPageSize());

    // 실제로 size + 1개를 조회하여 다음 페이지 존재 여부 확인
    int size = pageable.getPageSize();
    Pageable queryPageable = Pageable.ofSize(size + 1);
    List<Message> messages;

    if (cursor == null || cursor.isEmpty()) {
      // 첫 페이지 조회
      messages = messageRepository.findFirstPageByChannelIdWithAuthorAndAttachments(channelId, queryPageable);
    } else {
      // 커서 이후 메시지 조회 (createdAt 기준)
      try {
        Instant cursorTime = Instant.parse(cursor);
        messages = messageRepository.findAllByChannelIdAfterCursorTimeWithAuthorAndAttachments(channelId, cursorTime,
            queryPageable);
      } catch (Exception e) {
        log.warn("잘못된 커서 형식: {}, 첫 페이지로 조회", cursor);
        messages = messageRepository.findFirstPageByChannelIdWithAuthorAndAttachments(channelId, queryPageable);
      }
    }

    // 다음 페이지 존재 여부 확인
    boolean hasNext = messages.size() > size;
    if (hasNext) {
      // 실제 반환할 데이터는 size만큼만
      messages = messages.subList(0, size);
    }

    // 다음 커서 계산 (마지막 메시지의 createdAt)
    String nextCursor = null;
    if (hasNext && !messages.isEmpty()) {
      nextCursor = messages.get(messages.size() - 1).getCreatedAt().toString();
    }

    // DTO 변환
    List<MessageDto> messageDtos = mapperFacade.toMessageDtoList(messages);

    log.info("메시지 커서 페이징 조회 완료 - 채널ID: {}, 조회된 메시지: {}, 다음 페이지 존재: {}",
        channelId, messageDtos.size(), hasNext);

    return pageMapper.toCursorPageResponse(messageDtos, nextCursor, size, hasNext, null);
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();

    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));

    message.update(newContent);

    return mapperFacade.toDto(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new CustomException.MessageNotFoundException("Message with id " + messageId + " not found"));

    messageRepository.delete(message);
  }
}
