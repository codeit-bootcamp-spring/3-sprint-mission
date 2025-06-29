package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto createMessage(MessageCreateRequest messageRequest,
        List<BinaryContentCreateRequest> binaryContentRequests) {
        log.debug("메시지 생성 로직 시작 - 채널ID: {}, 작성자ID: {}", messageRequest.channelId(),
            messageRequest.authorId());

        Channel channel = channelRepository.findById(messageRequest.channelId())
            .orElseThrow(() -> {
                log.error("메시지 생성 실패 - 존재하지 않는 채널ID: {}", messageRequest.channelId());
                return new DiscodeitException(
                    ErrorCode.CHANNEL_NOT_FOUND,
                    Map.of("channelId", messageRequest.channelId())
                );
            });

        User author = userRepository.findById(messageRequest.authorId())
            .orElseThrow(() -> {
                log.error("메시지 생성 실패 - 존재하지 않는 유저ID: {}", messageRequest.authorId());
                return new UserNotFoundException(
                    ErrorCode.USER_NOT_FOUND,
                    Map.of("userId", messageRequest.authorId())
                );
            });

        List<BinaryContent> attachmentIds = new ArrayList<>();

        if (binaryContentRequests != null) {
            for (BinaryContentCreateRequest fileRequest : binaryContentRequests) {
                if (!fileRequest.isValid()) {
                    log.warn("첨부파일 유효성 검사 실패 - 파일명: {}", fileRequest.fileName());
                    throw new DiscodeitException(
                        ErrorCode.BINARY_CONTENT_INVALID,
                        Map.of("fileName", fileRequest.fileName())
                    );
                }
                BinaryContent binaryContent = binaryContentRepository.save(
                    new BinaryContent(fileRequest.fileName(), fileRequest.size(),
                        fileRequest.contentType()));

                binaryContentStorage.put(binaryContent.getId(), fileRequest.bytes());
                attachmentIds.add(binaryContent);
                log.debug("첨부파일 저장 완료 - 파일ID: {}", binaryContent.getId());
            }
        }

        Message message = new Message(
            messageRequest.content(),
            channel,
            author,
            attachmentIds
        );
        Message newMessage = messageRepository.save(message);

        return messageMapper.toDto(newMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
        Pageable pageable) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error("메시지 목록 조회 실패 - 존재하지 않는 채널 ID: {}", channelId);
                return new DiscodeitException(
                    ErrorCode.CHANNEL_NOT_FOUND,
                    Map.of("channelId", channelId)
                );
            });

        Slice<Message> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
            Optional.ofNullable(createAt).orElse(Instant.now()),
            pageable);

        Slice<MessageDto> mappedSlice = slice.map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!mappedSlice.getContent().isEmpty()) {
            nextCursor = mappedSlice.getContent().get(mappedSlice.getContent().size() - 1)
                .createdAt();
        }

        return pageResponseMapper.fromSlice(mappedSlice, nextCursor);
    }

    @Override
    @Transactional
    public MessageDto updateMessage(UUID messageId, MessageUpdateRequest request) {
        log.debug("메시지 수정 로직 시작 - 메시지ID: {}", messageId);
        Message msg = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.error("메시지 수정 실패 - 존재하지 않는 메시지ID: {}", messageId);
                return new DiscodeitException(
                    ErrorCode.MESSAGE_NOT_FOUND,
                    Map.of("messageId", messageId)
                );
            });

        msg.updateContent(request.newContent());
        Message updatedMessage = messageRepository.save(msg);
        return messageMapper.toDto(updatedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageId, UUID senderId) {
        log.debug("메시지 삭제 로직 시작 - 메시지ID: {}", messageId);
        Message msg = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.error("메시지 삭제 실패 - 존재하지 않는 메시지ID: {}", messageId);
                return new DiscodeitException(
                    ErrorCode.MESSAGE_NOT_FOUND,
                    Map.of("messageId", messageId)
                );
            });

        if (!msg.getAuthor().getId().equals(senderId)) {
            log.warn("메시지 삭제 권한 없음 - 메시지ID: {}, 요청자ID: {}", messageId, senderId);
            throw new DiscodeitException(
                ErrorCode.MESSAGE_DELETE_FORBIDDEN,
                Map.of("messageId", messageId, "senderId", senderId)
            );
        }

        List<BinaryContent> attachments = msg.getAttachments();
        for (BinaryContent attachment : attachments) {
            binaryContentRepository.deleteById(attachment.getId());
            log.debug("첨부파일 삭제 완료 - 파일ID: {}", attachment.getId());
        }

        messageRepository.deleteById(messageId);
    }
}
