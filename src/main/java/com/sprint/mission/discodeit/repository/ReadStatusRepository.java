package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

  /**
   * ReasStatus id로 조회
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
  List<ReadStatus> findByUserId(UUID userId);

  /**
   * 채널 id로 조회
   *
   * @param channelId UUID
   * @return List<ReadStatus>
   */
  List<ReadStatus> findByChannelId(UUID channelId);

  /**
   * 모든 ReadSatatus 조회
   *
   * @return List<ReadStatus>
   */
  List<ReadStatus> findAll();

  /**
   * ReadStatus 저장
   *
   * @param readStatus ReadStatus
   * @return ReadStatus
   */
  ReadStatus save(ReadStatus readStatus);

  /**
   * ReadStatus id로 삭제
   *
   * @param id UUID
   */
  void deleteById(UUID id);
}
