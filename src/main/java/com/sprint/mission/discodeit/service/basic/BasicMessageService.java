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
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
     */
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest createRequest,
            List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        User user = userRepository.findById(createRequest.authorId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        Channel channel = channelRepository.findById(createRequest.channelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));

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
                .content(createRequest.content())
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
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
            Pageable pageable) {

        int pageSize = (pageable != null && pageable.isPaged()) ? pageable.getPageSize() : 50;

        PageRequest pageRequest = PageRequest.of(0, pageSize + 1,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Slice<Message> messageSlice = (cursor != null)
                ? messageRepository.findAllByChannel_IdAndCreatedAtBefore(channelId, cursor,
                pageRequest)
                : messageRepository.findAllByChannel_Id(channelId, pageRequest);

        return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toDto));
    }

    /**
     * 주어진 id에 해당하는 메시지 조회
     *
     * @param messageId 조회할 메시지의 ID
     * @return 조회된 메시지
     */
    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지입니다."));

        return messageMapper.toDto(message);
    }

    /**
     * 주어진 ID에 해당하는 메시지를 수정 요청 DTO의 값으로 수정
     *
     * @param messageId     수정 대상 메시지ID
     * @param updateRequest 수정 요청 DTO
     * @return 수정된 메시지
     */
    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest updateRequest) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지입니다."));

        message.update(updateRequest.newContent());

        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    /**
     * 주어진 id에 해당하는 메시지 삭제
     *
     * @param messageId 삭제할 메시지 ID
     */
    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지입니다."));

        List<UUID> binaryContentIds = message.getAttachments().stream()
                .map(BinaryContent::getId)
                .toList();

        binaryContentRepository.deleteAllByIdIn(binaryContentIds);
        messageRepository.deleteById(messageId);
    }
}
