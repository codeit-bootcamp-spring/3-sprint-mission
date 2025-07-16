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
import com.sprint.mission.discodeit.exception.message.AuthorNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
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

        log.info("메시지 생성 요청: channelId={}, authorId={}, 첨부파일 개수={}",
            messageCreateRequest.channelId(),
            messageCreateRequest.authorId(),
            binaryContentCreateRequests.size());

        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("채널 조회 실패: id={}", channelId);
                return new ChannelNotFoundException(channelId);
            });

        User author = userRepository.findById(authorId)
            .orElseThrow(() -> {
                log.warn("작성자 조회 실패: id={}", authorId);
                return new AuthorNotFoundException(authorId);
            });

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
            .map(attachmentRequest -> {
                String fileName = attachmentRequest.fileName();
                String contentType = attachmentRequest.contentType();
                byte[] bytes = attachmentRequest.bytes();

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);

                log.debug("첨부파일 저장 완료: id={}, fileName={}", binaryContent.getId(), fileName);
                return binaryContent;
            })
            .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(content, channel, author, attachments);
        messageRepository.save(message);

        log.info("메시지 생성 완료: id={}, 채널={}, 작성자={}", message.getId(), channelId, authorId);
        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        log.debug("메시지 조회 요청: id={}", messageId);

        return messageRepository.findById(messageId)
            .map(messageMapper::toDto)
            .orElseThrow(() -> {
                log.warn("메시지 조회 실패: 존재하지 않는 ID={}", messageId);
                return new MessageNotFoundException(messageId);
            });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
        Pageable pageable) {
        log.debug("채널 메시지 목록 조회 요청: channelId={}, createAt={}", channelId, createAt);

        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(
            channelId,
            Optional.ofNullable(createAt).orElse(Instant.now()),
            pageable
        ).map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
        }

        log.debug("메시지 조회 결과 개수: {}", slice.getContent().size());
        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        log.info("메시지 수정 요청: id={}, newContent={}", messageId, request.newContent());

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.warn("수정 대상 메시지 없음: id={}", messageId);
                return new MessageNotFoundException(messageId);
            });

        message.update(request.newContent());
        log.info("메시지 수정 완료: id={}", messageId);
        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        log.info("메시지 삭제 요청: id={}", messageId);

        if (!messageRepository.existsById(messageId)) {
            log.error("삭제 실패: 메시지 없음 - id={}", messageId);
            throw new MessageNotFoundException(messageId);
        }

        messageRepository.deleteById(messageId);
        log.info("메시지 삭제 완료: id={}", messageId);
    }
}