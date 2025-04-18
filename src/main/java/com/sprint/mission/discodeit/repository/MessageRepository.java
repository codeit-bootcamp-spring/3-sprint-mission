package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    // 전체 데이터 저장
    public void saveAll();
    // 특정 채널 데이터 추가 후 저장
    public void save(Message message);
    // 데이터 전체 조회
    public Map<UUID, Message> readAll();
    // 데이터 단건 조회(id)
    public Optional<Message> readById(UUID id);
    // 데이터 단건 조회(채널이름)
    public Optional<Message> readByMessageContent(String msgContent);
}
