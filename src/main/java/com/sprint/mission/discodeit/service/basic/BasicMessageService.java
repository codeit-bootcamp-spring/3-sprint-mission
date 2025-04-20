package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class BasicMessageService implements MessageService {
    private final Map<UUID, Message> messages = new HashMap<>();

    // 메시지 생성
    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId, content);  // 새 메시지 생성
        messages.put(message.getId(), message);                     // Map에 저장
        return message;                                             // 메시지 반환
    }

    // 메시지 생성
    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        return create(authorId, channelId, content); // 재사용
    }

    // 메시지 조회
    @Override
    public Message findById(UUID id) {
        return messages.get(id); // 해당 ID의 메시지를 반환 (없으면 null)
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values()); // Map의 value들을 리스트로 반환
    }

    // 메시지 수정
    @Override
    public Message update(UUID id, String newContent) {
        Message message = messages.get(id);        // 메시지 조회
        if (message != null) {
            message.setContent(newContent);        // 내용 수정
            message.updateUpdatedAt();             // 수정 시간 갱신
        }
        return message;                            // 수정된 메시지 반환
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        messages.remove(id); // 해당 ID의 메시지를 Map에서 제거
    }
}