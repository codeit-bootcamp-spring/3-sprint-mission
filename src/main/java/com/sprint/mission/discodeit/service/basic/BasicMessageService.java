package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.ValidationException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    // 의존성
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;
    private final ApplicationEventPublisher eventPublisher;

    //메시지 생성
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest request,
            List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        // 첨부 파일 없으면 메시지 내용 필수
        validateMessageContent(request, binaryContentCreateRequests);

        User user = userRepository.findById(request.authorId())
                .orElseThrow(() -> new UserNotFoundException(request.authorId()));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));

        // 첨부파일 처리
        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(profileImage -> {
                    BinaryContent content = new BinaryContent(
                            profileImage.fileName(),
                            (long) profileImage.bytes().length,
                            profileImage.contentType()
                    );

                    binaryContentRepository.save(content);
//                    binaryContentStorage.put(content.getId(), profileImage.bytes());

                    // 이벤트 객체를 생성
                    BinaryContentCreatedEvent binaryContentEvent = BinaryContentCreatedEvent.now(
                        content.getId(),
                        profileImage.bytes()
                    );
                    // 생성된 이벤트 객체를 전달인자로 이벤트 발행
                    eventPublisher.publishEvent(binaryContentEvent);

                    return content;

                })
                .toList();

        Message newMessage = new Message(
                channel,
                user,
                request.content(),
                attachments
        );
        messageRepository.save(newMessage);

        // 이벤트 객체를 생성
        MessageCreatedEvent messageCreatedEvent = MessageCreatedEvent.now(
            newMessage.getId(),
            newMessage.getChannel().getId(),
            newMessage.getAuthor().getId()
        );
        // 생성된 이벤트 객체를 전달인자로 이벤트 발행
        eventPublisher.publishEvent(messageCreatedEvent);

        return messageMapper.toDto(newMessage);
    }

    // 채널의 메시지 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> getAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {

        Slice<Message> sliceMessage = messageRepository.findAllByChannelIdWithAuthor(channelId,
                Optional.ofNullable(cursor).orElse(Instant.now()), pageable);

        Slice<MessageDto> slice = sliceMessage.map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
        }

        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    // 메시지 아이디 이용한 조회
    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {

        return messageRepository.findById(messageId)
                .map(messageMapper::toDto)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
    }

    // 메시지 수정
    @PreAuthorize("@messageSecurityService.canEdit(#messageId, authentication.principal.id)")
    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {

        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        message.updateContent(newContent);

        return messageMapper.toDto(message);
    }

    // 메시지 삭제
    @PreAuthorize("@messageSecurityService.canDelete(#messageId, authentication.principal.id)")
    @Override
    @Transactional
    public void delete(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        messageRepository.deleteById(messageId);

    }

    private void validateMessageContent(MessageCreateRequest request, List<BinaryContentCreateRequest> attachments){

        boolean hasContent = request.content() !=null && !request.content().trim().isEmpty();
        boolean hasAttachments = attachments != null && !attachments.isEmpty();

        // 메시지 내용도 없고 첨부파일도 없는 경우
        if (!hasContent && !hasAttachments) {
            Map<String, Object> details = Map.of(
                    "메시지를 생성하려고 한 채널", request.channelId()
            );
            throw new ValidationException("메시지 내용 또는 첨부파일 중 하나는 필수입니다.", details);
        }
    }

}

