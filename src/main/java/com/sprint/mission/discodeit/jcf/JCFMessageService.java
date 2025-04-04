package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

// 실제 메시지를 저장하고 관리하는 JCFMessageService 클래스
public class JCFMessageService implements MessageService {

    // 메시지를 저장할 공간 (Map 자료구조 사용)
    // key는 UUID, value는 Message 객체
    private final Map<UUID, Message> data = new HashMap<UUID, Message>();

    @Override // 메시지 등록 기능 (Map에 저장)
    public void create(Message message) {
        data.put(message.getId(), message);
    }

    @Override // 메시지를 ID로 조회하는 기능
    public Message getById(UUID id) {
        return data.get(id);
    }

    @Override // 저장된 모든 메시지를 리스트로 가져오는 기능
    public List<Message> getAll() {
        return new ArrayList<>(data.values()); // Map의 값들만 모아서 리스트로 반환
    }

    @Override  // 메시지를 수정하는 기능
    public void update(Message message) {
        data.put(message.getId(), message);
    }

    @Override // 메시지를 삭제하는 기능
    public void delete(UUID id) {
        data.remove(id);
    }
}
