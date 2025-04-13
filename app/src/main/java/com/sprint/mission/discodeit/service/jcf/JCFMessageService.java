package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

// Java Collections Framework 기반 메시지 서비스 구현 클래스
public class JCFMessageService implements MessageService {

    // 메시지 데이터를 저장할 Map (key: UUID, value: Message)
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        // 새로운 메시지 생성 후 저장소에 추가
        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        // 주어진 ID에 해당하는 메시지 반환 (없으면 null)
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        // 저장된 모든 메시지를 리스트로 반환
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String newContent) {
        // ID로 메시지를 찾아 내용 수정
        Message message = data.get(id);
        if (message != null) {
            message.updateContent(newContent);
        }
        return message; // null 또는 수정된 메시지 반환
    }

    @Override
    public void delete(UUID id) {
        // ID에 해당하는 메시지 삭제
        data.remove(id);
    }
}