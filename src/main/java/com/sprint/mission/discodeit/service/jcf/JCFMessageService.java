package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    // 메시지 데이터 저장 Map
    private final Map<UUID, Message> data;

    // 생성자: 데이터 저장용 Map 초기화
    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    // 메시지 생성 및 저장
    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    // 메시지 목록 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }
    // ID로 메시지 조회
    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }


    // 메시지 내용 수정
    @Override
    public void update(UUID id, String newContent) {
        Message message = data.get(id);
        if (message != null) {
            message.updateContent(newContent);
        }
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
