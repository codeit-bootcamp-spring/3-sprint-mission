package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 등록
    void create(Message message);

    // 단일 메시지 조회
    Message getById(UUID id);

    //전체 메시지 조회
    List<Message> getAll();

    // 메시지 수정
    void update(Message message);

    // 메시지 삭제
    void delete(UUID id);
}
