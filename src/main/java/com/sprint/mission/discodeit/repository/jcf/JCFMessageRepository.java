package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> storage = new HashMap<>();

    // 메시지 저장 (신규 생성 또는 수정 시 호출)
    @Override
    public void save(Message message) {
        storage.put(message.getId(), message); // ID 중복 시 덮어씀
    }

    // 메시지 조회
    @Override
    public Message findById(UUID id) {
        return storage.get(id); // 해당 ID의 메시지 반환 (없으면 null)
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(storage.values()); // Map의 value들을 리스트로 변환
    }

    // 메시지 업데이트
    @Override
    public void update(Message message) {
        storage.put(message.getId(), message); // ID 기준으로 덮어쓰기
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        storage.remove(id); // 해당 ID에 해당하는 메시지 삭제
    }
}