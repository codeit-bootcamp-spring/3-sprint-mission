package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.annotation.Logging;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
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
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Logging
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentStorage binaryContentStorage;

    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    /**
     * 주어진 생성 요청 DTO를 기반으로 메시지 생성
     *
     * @param createRequest               메시지 생성 요청 DTO
     * @param binaryContentCreateRequests 첨부파일 생성 요청 DTO 리스트
     * @return 생성된 메시지
     * @throws UserNotFoundException    작성자 ID가 일치하지 않는 경우
     * @throws ChannelNotFoundException 채널 ID가 일치하지 않는 경우
     */
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest createRequest,
            List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        UUID authorId = createRequest.authorId();
        UUID channelId = createRequest.channelId();
        String content = createRequest.content();

        log.info("메시지 생성 요청: 작성자 = {}, 채널 = {}, 내용 = {}", authorId,
                channelId, content);

        log.info("첨부파일 요청: {}", binaryContentCreateRequests);

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> UserNotFoundException.byId(authorId));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        List<BinaryContent> binaryContents = new ArrayList<>();

        for (BinaryContentCreateRequest dto : binaryContentCreateRequests) {
            BinaryContent binaryContent = BinaryContent.builder()
                    .fileName(dto.fileName())
                    .contentType(dto.contentType())
                    .size(((long) dto.bytes().length))
                    .build();

            binaryContents.add(binaryContent);
        }

        binaryContentRepository.saveAll(binaryContents);

        for (int i = 0; i < binaryContents.size(); i++) {
            BinaryContent savedContent = binaryContents.get(i);
            byte[] fileBytes = binaryContentCreateRequests.get(i).bytes();
            binaryContentStorage.put(savedContent.getId(), fileBytes);
        }

        Message msg = Message.builder()
                .content(content)
                .author(user)
                .channel(channel)
                .attachments(binaryContents)
                .build();

        messageRepository.save(msg);
        return messageMapper.toDto(msg);
    }

    /**
     * 주어진 채널ID에 해당하는 메시지 전체 조회
     *
     * @param channelId 조회할 메시지의 채널ID
     * @return 조회된 메시지리스트
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
            Pageable pageable) {

        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId);
        }

        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                        Optional.ofNullable(createAt).orElse(Instant.now()),
                        pageable)
                .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                    .createdAt();
        }

        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    /**
     * 주어진 id에 해당하는 메시지 조회
     *
     * @param messageId 조회할 메시지의 ID
     * @return 조회된 메시지
     * @throws MessageNotFoundException 메시지가 존재하지 않는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        return messageMapper.toDto(message);
    }

    /**
     * 주어진 ID에 해당하는 메시지를 수정 요청 DTO의 값으로 수정 l
     *
     * @param messageId     수정 대상 메시지ID
     * @param updateRequest 수정 요청 DTO
     * @return 수정된 메시지
     * @throws MessageNotFoundException 메시지가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest updateRequest) {
        log.info("메시지 수정 요청: 내용 = {}", updateRequest.newContent());

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        message.update(updateRequest.newContent());
        return messageMapper.toDto(message);
    }

    /**
     * 주어진 id에 해당하는 메시지 삭제
     *
     * @param messageId 삭제할 메시지 ID
     * @throws MessageNotFoundException 메시지가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void delete(UUID messageId) {
        log.info("메시지 삭제 요청: ID = {}", messageId);

        if (!messageRepository.existsById(messageId)) {
            log.warn("메시지 삭제 실패: 존재하지 않는 메시지: ID = {}", messageId);
            throw new MessageNotFoundException(messageId);
        }

        messageRepository.deleteById(messageId);

        log.info("메시지 삭제 완료: ID = {}", messageId);
    }
}
