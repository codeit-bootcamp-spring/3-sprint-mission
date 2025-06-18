package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  // N+1 문제 해결: ReadStatus 조회 시 사용자 정보를 함께 조회
  @Query("SELECT rs FROM ReadStatus rs " +
      "JOIN FETCH rs.user " +
      "WHERE rs.user.id = :userId")
  List<ReadStatus> findAllByUserIdWithUser(@Param("userId") UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  // N+1 문제 해결: ReadStatus 조회 시 사용자 정보를 함께 조회
  @Query("SELECT rs FROM ReadStatus rs " +
      "JOIN FETCH rs.user " +
      "WHERE rs.channel.id = :channelId")
  List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
