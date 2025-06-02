package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
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
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public MessageDto create(MessageCreateRequest req, List<BinaryContentCreateRequest> files) throws IOException {
        User author = userRepository.findById(req.authorId())
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 존재하지 않습니다."));
        Channel channel = channelRepository.findById(req.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));

        List<BinaryContent> attachments = files.stream()
                .map(fileReq -> {
                    BinaryContent meta = new BinaryContent();
                    meta.setFileName(fileReq.fileName());
                    meta.setSize((long) fileReq.bytes().length);
                    meta.setContentType(fileReq.contentType());
                    BinaryContent savedMeta = binaryContentRepository.save(meta);

                    try {
                        UUID fileId = savedMeta.getId();
                        binaryContentStorage.put(fileId, fileReq.bytes());
                    } catch (IOException e) {
                        throw new RuntimeException("첨부파일 저장 중 오류가 발생했습니다", e);
                    }

                    return savedMeta;
                })
                .toList();

        Message message = new Message(req.content(), channel, author, attachments);
        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        Message entity = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지는 없습니다."));
        return messageMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page) {
        Pageable pageable = PageRequest.of(page, 50, Sort.by("createdAt").descending());
        Page<Message> paged = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(channelId, pageable);
        Page<MessageDto> dtoPage = paged.map(messageMapper::toDto);
        return pageResponseMapper.fromPage(dtoPage);
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest req) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지는 없습니다."));
        message.update(req.newContent());
        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지는 없습니다."));
        binaryContentRepository.deleteAll(message.getAttachments());
        messageRepository.deleteById(messageId);
    }
}