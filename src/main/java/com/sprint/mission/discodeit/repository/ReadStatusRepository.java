package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    // 읽음상태 저장
    ReadStatus saveReadStatus(ReadStatus readStatus);

    // id로 조회
    Optional<ReadStatus> findReadStatusById(UUID id);

    // 유저 아이디로 조회
    List<ReadStatus> findUserReadStatus(UUID userId);

    // 채널 아이디로 조회
    List<ReadStatus> findReadStatusByChannelId(UUID channelId);

    // 수정
    void updateReadStatus(ReadStatus readStatus);

    // 읽은 메시지 취소 -> 다 읽지 않음으로
    void deleteReadStatusById(UUID id);

    // 채널 아이디로 삭제
    void deleteByChannelId(UUID channelId);
}