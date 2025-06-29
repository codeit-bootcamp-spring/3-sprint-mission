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
import com.sprint.mission.discodeit.exception.message.MessageEmptyException;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest request,
        List<BinaryContentCreateRequest> attachmentRequests) {
        UUID authorId = request.authorId();
        UUID channelId = request.channelId();
        log.info("[message] 생성 요청: authorId={}, channelId={}", authorId, channelId);

        User author = userRepository.findById(authorId)
            .orElseThrow(() -> {
                log.warn("[message] 생성 실패 - 존재하지 않는 userId: userId={}", authorId);
                return new UserNotFoundException(authorId);
            });

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("[message] 생성 실패 - 존재하지 않는 channelId: channelId={}",
                    channelId);
                return new ChannelNotFoundException(channelId);
            });

        List<BinaryContent> attachments = createAttachment(attachmentRequests);

        Message message = new Message(
            request.content(),
            channel,
            author,
            attachments
        );

        validateContent(request.content(), attachments);
        messageRepository.save(message);
        log.info("[message] 생성 완료: messageId={}, authorId={}, channelId={}",
            message.getId(), authorId, channelId);

        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID id) {
        log.info("[message] 조회 요청: id={}", id);

        return messageRepository.findById(id)
            .map(messageMapper::toDto)
            .orElseThrow(() -> {
                log.warn("[message] 조회 실패 - 존재하지 않는 id: id={}", id);
                return new MessageNotFoundException(id);
            });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
        Pageable pageable) {
        Slice<MessageDto> slice = messageRepository.findByChannelIdAndCreatedAtLessThan(channelId,
                Optional.ofNullable(createAt).orElse(Instant.now()), pageable)
            .map(messageMapper::toDto);

        log.info("[message] 전체 조회 요청: channelId={}, size={}", channelId, slice.getContent().size());
        log.debug("[message] Slice 정보: {}", slice);

        Instant nextCursor = null;
        if (slice.hasContent()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                .createdAt();
        }

        log.info("[message] 전체 조회 응답: channelId={}, 결과 개수={}, nextCursor={}",
            channelId, slice.getContent().size(), nextCursor);
        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        log.info("[message] 수정 요청: messageId={}, newContent={}", messageId, request.newContent());

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.warn("[message] 수정 실패 - 존재하지 않는 id: id={}", messageId);
                return new MessageNotFoundException(messageId);
            });

        message.update(newContent);
        log.info("[message] 수정 완료: messageId={}, newContent={}", messageId, request.newContent());

        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            log.warn("[message] 삭제 실패 - 존재하지 않는 id: id={}", messageId);
            throw new MessageNotFoundException(messageId);
        }

        messageRepository.deleteById(messageId);
        log.info("[message] 삭제 완료: id={}", messageId);
    }

    /**
     * 첨부파일 생성 메서드
     */
    private List<BinaryContent> createAttachment(
        List<BinaryContentCreateRequest> attachmentRequests) {
        List<BinaryContent> attachments = attachmentRequests == null ? List.of() :
            attachmentRequests.stream()
                .map(
                    req -> {
                        BinaryContent binaryContent = new BinaryContent(
                            req.fileName(),
                            (long) req.bytes().length,
                            req.contentType());
                        BinaryContent savedAttachment = binaryContentRepository.save(binaryContent);
                        binaryContentStorage.put(savedAttachment.getId(), req.bytes());
                        return savedAttachment;
                    })
                .toList();
        log.debug("[message] 첨부파일 요청 수: {}",
            attachmentRequests != null ? attachmentRequests.size() : 0);

        return attachments;
    }

    /**
     * 엔티티에 있던 검증 로직 서비스로 이동
     * <p>
     * 메시지의 내용과 첨부파일이 없으면 예외를 던짐
     */
    private void validateContent(String content, List<BinaryContent> attachments) {
        boolean isContentEmpty = (content == null || content.trim().isEmpty());
        boolean hasNoAttachments = (attachments == null || attachments.isEmpty());

        if (isContentEmpty && hasNoAttachments) {
            throw new MessageEmptyException();
        }
    }
}
