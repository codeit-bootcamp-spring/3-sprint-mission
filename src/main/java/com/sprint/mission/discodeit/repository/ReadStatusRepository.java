package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    // 메시지를 읽었다는 상태 저장
    ReadStatus save(ReadStatus readStatus);

    // 특정 유저가 읽은 모든 메시지 상태 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 특정 메시지를 읽은 모든 유저 상태 조회
    List<ReadStatus> findAllByMessageId(UUID messageId);

    // 전체 읽음 상태 조회
    List<ReadStatus> findAll();

    // 읽음 상태 삭제
    void delete(UUID readStatusId);
}