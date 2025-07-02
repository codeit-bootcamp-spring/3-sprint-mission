package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.data.MessageDto;
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
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메시지 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>메시지 생성, 수정, 삭제, 조회 기능을 제공하며, 이미지 첨부 기능을 지원합니다.</p>
 */
@Slf4j
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

    private static final String SERVICE_NAME = "[MessageService] ";

    /**
     * 새로운 메시지를 생성합니다.
     *
     * <p>텍스트 메시지와 함께 이미지 첨부가 가능하며, 첨부파일은 스토리지에 저장됩니다.</p>
     *
     * @param messageCreateRequest 메시지 생성 요청 정보
     * @param binaryContentCreateRequests 첨부파일 생성 요청 목록
     * @return 생성된 메시지 DTO
     * @throws NoSuchElementException 채널 또는 사용자가 존재하지 않을 경우
     */
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();
        
        log.info(SERVICE_NAME + "메시지 생성 시도: channelId={}, authorId={}, 첨부파일 개수={}",
                channelId, authorId, binaryContentCreateRequests.size());

        // 채널과 사용자 존재 여부 확인 및 조회
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "채널을 찾을 수 없음: channelId={}", channelId);
                return new MessageNotFoundException("채널을 찾을 수 없습니다.");
            });
            
        User user = userRepository.findById(authorId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "사용자를 찾을 수 없음: authorId={}", authorId);
                return new MessageNotFoundException("사용자를 찾을 수 없습니다.");
            });

        log.debug(SERVICE_NAME + "채널 및 사용자 조회 성공: channelId={}, authorId={}", channelId, authorId);

        // 첨부파일 처리
        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
            .map(attachmentRequest -> {
                String fileName = attachmentRequest.fileName();
                String contentType = attachmentRequest.contentType();
                byte[] bytes = attachmentRequest.bytes();

                log.debug(SERVICE_NAME + "첨부파일 처리 중: fileName={}, contentType={}, size={}",
                        fileName, contentType, bytes.length);

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(savedBinaryContent.getId(), attachmentRequest.bytes());
                
                log.debug(SERVICE_NAME + "첨부파일 저장 완료: binaryContentId={}", savedBinaryContent.getId());
                return savedBinaryContent;
            })
            .toList();

        // 메시지 생성 및 저장
        String content = messageCreateRequest.content();
        Message message = new Message(
            content,
            channel,
            user,
            attachments
        );
        Message savedMessage = messageRepository.save(message);

        log.info(SERVICE_NAME + "메시지 생성 완료: messageId={}, channelId={}, 첨부파일 개수={}",
                savedMessage.getId(), channelId, attachments.size());

        return messageMapper.toDto(savedMessage);
    }

    /**
     * 특정 메시지를 조회합니다.
     *
     * @param messageId 조회할 메시지 ID
     * @return 조회된 메시지 DTO
     * @throws NoSuchElementException 메시지가 존재하지 않을 경우
     */
    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        log.info(SERVICE_NAME + "메시지 조회 시도: messageId={}", messageId);
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "메시지를 찾을 수 없음: messageId={}", messageId);
                return new MessageNotFoundException("메시지를 찾을 수 없습니다.");
            });

        log.info(SERVICE_NAME + "메시지 조회 완료: messageId={}, channelId={}",
                messageId, message.getChannel().getId());

        return messageMapper.toDto(message);
    }

    /**
     * 특정 채널의 메시지 목록을 페이지네이션으로 조회합니다.
     *
     * <p>커서 기반 페이지네이션을 사용하여 성능을 최적화합니다.</p>
     *
     * @param channelId 채널 ID
     * @param createdAt 커서 기반 페이지네이션을 위한 생성 시간
     * @param pageable 페이지네이션 정보
     * @return 페이지네이션된 메시지 목록
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelIdWithAuthor(UUID channelId, Instant createdAt, Pageable pageable) {
        log.info(SERVICE_NAME + "채널 메시지 목록 조회 시도: channelId={}, createdAt={}, page={}, size={}",
                channelId, createdAt, pageable.getPageNumber(), pageable.getPageSize());

        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(
                channelId,
                Optional.ofNullable(createdAt)
                        .orElse(Instant.now()),
                pageable
                )
                .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                    .createdAt();
        }

        PageResponse<MessageDto> pageResponse = pageResponseMapper.fromSlice(slice, nextCursor);

        log.info(SERVICE_NAME + "채널 메시지 목록 조회 완료: channelId={}, 조회된 메시지 수={}, 다음 커서={}",
                channelId, slice.getContent().size(), nextCursor);

        return pageResponse;
    }

    /**
     * 메시지 내용을 수정합니다.
     *
     * @param messageId 수정할 메시지 ID
     * @param request 메시지 수정 요청 정보
     * @return 수정된 메시지 DTO
     * @throws NoSuchElementException 메시지가 존재하지 않을 경우
     */
    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        
        log.info(SERVICE_NAME + "메시지 수정 시도: messageId={}, newContent={}", messageId, newContent);
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "메시지를 찾을 수 없음: messageId={}", messageId);
                return new MessageNotFoundException("메시지를 찾을 수 없습니다.");
            });
            
        message.update(newContent);
        messageRepository.save(message);

        log.info(SERVICE_NAME + "메시지 수정 완료: messageId={}", messageId);

        return messageMapper.toDto(message);
    }

    /**
     * 메시지를 삭제합니다.
     *
     * <p>메시지와 함께 첨부된 파일들도 함께 삭제됩니다.</p>
     *
     * @param messageId 삭제할 메시지 ID
     * @throws NoSuchElementException 메시지가 존재하지 않을 경우
     */
    @Override
    @Transactional
    public void delete(UUID messageId) {
        log.info(SERVICE_NAME + "메시지 삭제 시도: messageId={}", messageId);
        
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "메시지를 찾을 수 없음: messageId={}", messageId);
                return new MessageNotFoundException("메시지를 찾을 수 없습니다.");
            });

        // 첨부파일 삭제
        int attachmentCount = message.getAttachments().size();
        message.getAttachments()
            .stream()
            .map(BinaryContent::getId)
            .forEach(binaryContentRepository::deleteById);

        // 메시지 삭제
        messageRepository.deleteById(messageId);

        log.info(SERVICE_NAME + "메시지 삭제 완료: messageId={}, 삭제된 첨부파일 개수={}",
                messageId, attachmentCount);
    }
}
