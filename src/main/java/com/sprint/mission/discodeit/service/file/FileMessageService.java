package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

// 파일 기반 메시지 서비스 구현체
public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository = new FileMessageRepository();

    // 메시지 생성 (UUID, UUID, String 순서)
    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId, content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        return create(authorId, channelId, content); // 위 메서드 재사용
    }

    // 메시지 ID로 조회
    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    // 전체 메시지 목록 조회
    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    // 메시지 내용 수정
    @Override
    public Message update(UUID id, String newContent) {
        Message message = messageRepository.findById(id);
        if (message != null) {
            message.setContent(newContent);       // 메시지 내용 변경
            message.updateUpdatedAt();            // 수정 시간 갱신
            messageRepository.update(message);    // 저장 반영
        }
        return message;
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        messageRepository.delete(id);
    }
}