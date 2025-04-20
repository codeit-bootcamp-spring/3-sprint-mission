package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

// Message 저장소를 위한 공통 인터페이스
public interface MessageRepository {
    void save(Message message);            // 메시지 저장
    Message findById(UUID id);             // ID로 메시지 조회
    List<Message> findAll();               // 전체 메시지 조회
    void update(Message message);          // 메시지 수정
    void delete(UUID id);                  // 메시지 삭제
}