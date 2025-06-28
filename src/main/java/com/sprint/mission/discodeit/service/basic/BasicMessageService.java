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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.InvalidMessageContentException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        boolean hasText = StringUtils.hasText(messageCreateRequest.content());
        boolean hasAttachments =
            binaryContentCreateRequests != null && !binaryContentCreateRequests.isEmpty();

        if (!hasText && !hasAttachments) {
            throw new InvalidMessageContentException(messageCreateRequest.content());
        }

        log.info("메시지 생성 중 - channelId: {}, authorId: {}, 첨부파일 수: {}",
            channelId, authorId, Optional.ofNullable(binaryContentCreateRequests)
                .map(List::size)
                .orElse(0));

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("메시지 생성 실패 - 존재하지 않는 채널 ID: {}", channelId);
                return new ChannelNotFoundException(channelId);
            });
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 사용자 ID: {}", authorId);
                    return new UserNotFoundException(authorId);
                }
            );

        List<BinaryContent> attachments = hasAttachments ?
            binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    log.debug("메시지 첨부파일 저장 - 파일명: {}, 크기: {} bytes, 타입: {}",
                        fileName, bytes.length, contentType);

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                        contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .toList()
            : List.of();

        String content = messageCreateRequest.content();
        Message message = new Message(
            content,
            channel,
            author,
            attachments
        );

        messageRepository.save(message);

        log.info("메시지 생성 완료 - messageId: {}, channelId: {}, 첨부파일 수: {}",
            message.getId(), channelId, attachments.size());

        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        return messageRepository.findById(messageId)
            .map(messageMapper::toDto)
            .orElseThrow(
                () -> new MessageNotFoundException(messageId));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
        Pageable pageable) {

        log.info("메시지 목록 조회 요청 - channelId: {}, createdAt: {}, pageSize: {}",
            channelId, createAt, pageable.getPageSize());

        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                Optional.ofNullable(createAt).orElse(Instant.now()),
                pageable)
            .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                .createdAt();
        }

        log.info("메시지 목록 조회 성공 - 조회 수: {}, nextCursor: {}",
            slice.getNumberOfElements(), nextCursor);

        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();

        log.info("메시지 수정 중 - messageId: {}", messageId);

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.warn("메시지 수정 실패 - 존재하지 않는 메시지 ID: {}", messageId);
                return new MessageNotFoundException(messageId);
            });
        message.update(newContent);

        log.info("메시지 수정 완료 - messageId: {}", messageId);

        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        log.info("메시지 삭제 시작 - messageId: {}", messageId);

        if (!messageRepository.existsById(messageId)) {
            log.warn("메시지 삭제 실패 - 존재하지 않는 메시지 ID: {}", messageId);
            throw new MessageNotFoundException(messageId);
        }

        messageRepository.deleteById(messageId);

        log.info("메시지 삭제 완료 - messageId: {}", messageId);
    }
}
