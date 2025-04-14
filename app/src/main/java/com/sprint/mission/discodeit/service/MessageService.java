package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService { // 메시지에 대한 서비스 인터페이스
    Message create(UUID userId, UUID channelId, String content); // 메시지 생성
    Message findById(UUID id); // ID로 메시지 조회
    List<Message> findAll(); // 모든 메시지 조회
    Message update(UUID id, String newContent); // 메시지 내용 수정
    void delete(UUID id); // 메시지 삭제
}
