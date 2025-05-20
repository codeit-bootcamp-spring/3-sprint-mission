package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

  /**
   * ReadStatus 삽입 (이미 존재 시 예외)
   *
   * @param readStatus ReadStatus
   */
  void insert(ReadStatus readStatus);

  /**
   * ReadStatus id로 조회
   *
   * @param id UUID
   * @return Optional<ReadStatus>
   */
  Optional<ReadStatus> findById(UUID id);

  /**
   * 사용자 id, 채널 id로 조회
   *
   * @param userId    UUID
   * @param channelId UUID
   * @return Optional<ReadStatus>
   */
  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  /**
   * 사용자 id로 조회
   *
   * @param userId UUID
   * @return List<ReadStatus>
   */
  List<ReadStatus> findAllByUserId(UUID userId);

  /**
   * 채널 id로 조회
   *
   * @param channelId UUID
   * @return List<ReadStatus>
   */
  List<ReadStatus> findAllByChannelId(UUID channelId);

  /**
   * 모든 ReadStatus 조회
   *
   * @return List<ReadStatus>
   */
  List<ReadStatus> findAll();

  /**
   * ReadStatus 저장 또는 업데이트
   *
   * @param readStatus ReadStatus
   * @return ReadStatus
   */
  ReadStatus save(ReadStatus readStatus);

  /**
   * ReadStatus 업데이트 (존재하지 않으면 예외)
   *
   * @param readStatus ReadStatus
   */
  void update(ReadStatus readStatus);

  /**
   * ReadStatus id로 삭제
   *
   * @param id UUID
   */
  void delete(UUID id);
}
