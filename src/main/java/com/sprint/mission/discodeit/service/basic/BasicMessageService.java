package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    /**
     * 주어진 생성 요청 DTO(메시지, BinaryContent)를 기반으로 메시지 생성
     *
     * @param createRequestDTO               메시지 생성 요청 DTO
     * @param binaryContentCreateRequestsDTO 첨부파일(BinaryContent) 생성 요청 DTO 리스트
     * @return 생성된 메시지
     * @throws NoSuchElementException 작성자 또는 채널이 존재하지 않는 경우
     */
    @Override
    public Message create(MessageCreateRequest createRequestDTO,
            List<BinaryContentCreateRequest> binaryContentCreateRequestsDTO) {
        UUID authorId = createRequestDTO.authorId();
        UUID channelId = createRequestDTO.channelId();

        // 작성자, 채널 유효성 검사
        if (!userRepository.existsById(createRequestDTO.authorId())) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다.");
        }
        if (!channelRepository.existsById(createRequestDTO.channelId())) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }

        List<UUID> binaryContents = new ArrayList<>();
        for (BinaryContentCreateRequest dto : binaryContentCreateRequestsDTO) {
            String fileName = dto.fileName();
            String contentType = dto.contentType();
            byte[] bytes = dto.bytes();

            BinaryContent binaryContent = BinaryContent.builder()
                    .fileName(fileName)
                    .size((long) bytes.length)
                    .contentType(contentType)
                    .bytes(bytes)
                    .build();

            binaryContentRepository.save(binaryContent);
            binaryContents.add(binaryContent.getId());
        }

        String content = createRequestDTO.content();

        Message msg = Message.builder()
                .content(content)
                .authorId(authorId)
                .channelId(channelId)
                .attachmentIds(binaryContents)
                .build();

        messageRepository.save(msg);
        return msg;
    }

    /**
     * 주어진 채널ID에 해당하는 메시지 전체 조회
     *
     * @param channelId 조회할 메시지의 채널ID
     * @return 조회된 메시지리스트
     */
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    /**
     * 주어진 id에 해당하는 메시지 조회
     *
     * @param messageId 조회할 메시지의 ID
     * @return 조회된 메시지
     * @throws NoSuchElementException 해당 ID의 메시지가 존재하지 않는 경우
     */
    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));
    }

    /**
     * 주어진 ID에 해당하는 메시지를 수정 요청 DTO의 값으로 수정
     *
     * @param messageId        수정 대상 메시지ID
     * @param updateRequestDTO 수정 요청 DTO
     * @return 수정된 메시지
     * @throws NoSuchElementException 해당 ID의 메시지가 존재하지 않는 경우
     */
    @Override
    public Message update(UUID messageId, MessageUpdateRequest updateRequestDTO) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));

        String newContent = updateRequestDTO.newContent();

        msg.update(newContent);

        messageRepository.save(msg);
        return msg;
    }

    /**
     * 주어진 id에 해당하는 메시지 삭제
     *
     * @param messageId 삭제할 메시지 ID
     * @throws NoSuchElementException 해당 ID의 메시지가 존재하지 않는 경우
     */
    @Override
    public void delete(UUID messageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));

        msg.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }
}
