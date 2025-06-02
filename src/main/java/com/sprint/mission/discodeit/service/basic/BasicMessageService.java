package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.struct.BinaryContentStructMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("basicMessageService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final BinaryContentStructMapper binaryContentMapper;

    @Override
    @Transactional
    public MessageResponseDto create(MessageRequestDto messageRequestDto,
                                     List<BinaryContentDto> binaryContentDtos) {
        User author = findUser(messageRequestDto.authorId());
        Channel channel = findChannel(messageRequestDto.channelId());

        // Dto -> Entity
        List<BinaryContent> binaryContents = convertBinaryContentDtos(binaryContentDtos);

        binaryContentRepository.saveAll(binaryContents);

        for (int i = 0; i < binaryContents.size(); i++) {
            UUID id = binaryContents.get(i).getId();
            byte[] bytes = binaryContentDtos.get(i).bytes();
            binaryContentStorage.put(id, bytes);
        }

        String content = messageRequestDto.content();

        Message message = Message.builder()
                .content(content)
                .author(author)
                .channel(channel)
                .attachments(new ArrayList<>())
                .build();

        message.updateAttachments(binaryContents);
        
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    public MessageResponseDto findById(UUID messageId) {
        Message message = findMessage(messageId);

        return messageMapper.toDto(message);
    }

    @Override
    public PageResponse<MessageResponseDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        int size = pageable.getPageSize();
        List<Message> messages;

        if (cursor == null) {
            messages = messageRepository.findPageByChannelId(channelId, pageable);
        } else {
            messages = messageRepository.findPageByChannelIdAndCursor(channelId, cursor, pageable);
        }

        boolean hasNext = messages.size() == size;
        Instant nextCursor = hasNext ? messages.get(messages.size() - 1).getCreatedAt() : null;

        List<MessageResponseDto> content = messages.stream()
                .map(messageMapper::toDto)
                .toList();

        return new PageResponse<>(content, nextCursor, size, hasNext, null);
    }

    @Override
    @Transactional
    public MessageResponseDto updateContent(UUID messageId, String content) {
        Message message = findMessage(messageId);

        message.updateContent(content);

        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void deleteById(UUID messageId) {
        Message message = findMessage(messageId);

        for (BinaryContent binaryContent : message.getAttachments()) {
            binaryContentRepository.deleteById(binaryContent.getId());
        }

        messageRepository.deleteById(messageId);
    }

    private List<BinaryContent> convertBinaryContentDtos(List<BinaryContentDto> binaryContentDtos) {
        return binaryContentDtos.stream()
                .map(binaryContentMapper::toEntity)
                .toList();
    }

    private Message findMessage(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(NotFoundMessageException::new);
    }

    private Channel findChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(NotFoundChannelException::new);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(NotFoundUserException::new);
    }
}
