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
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
    public MessageDto createMessage(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
            .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        User author = userRepository.findById(messageCreateRequest.authorId())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 ID입니다."));

        List<BinaryContent> attachmentIds = new ArrayList<>();

        if (binaryContentCreateRequests != null) {
            for (BinaryContentCreateRequest fileRequest : binaryContentCreateRequests) {
                if (!fileRequest.isValid()) {
                    throw new IllegalArgumentException("메세지에 첨부파일을 추가할 수 없습니다. 파일을 확인해주세요.");
                }
                BinaryContent binaryContent = binaryContentRepository.save(
                    new BinaryContent(fileRequest.fileName(), fileRequest.size(),
                        fileRequest.contentType()));

                binaryContentStorage.put(binaryContent.getId(), fileRequest.bytes());
                attachmentIds.add(binaryContent);
            }
        }

        Message message = new Message(
            messageCreateRequest.content(),
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
            .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

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
        Message msg = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        msg.updateContent(request.newContent());
        Message updatedMessage = messageRepository.save(msg);
        return messageMapper.toDto(updatedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageId, UUID senderId) {
        Message msg = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (!msg.getAuthor().getId().equals(senderId)) {
            throw new SecurityException("해당 메시지를 삭제할 권한이 없습니다.");
        }

        List<BinaryContent> attachments = msg.getAttachments();
        for (BinaryContent attachment : attachments) {
            binaryContentRepository.deleteById(attachment.getId());
        }

        messageRepository.deleteById(messageId);
    }
}
