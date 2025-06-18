package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  // N+1 문제 해결: ReadStatus 조회 시 사용자 정보를 함께 조회
  @Query("SELECT rs FROM ReadStatus rs " +
      "JOIN FETCH rs.user " +
      "WHERE rs.user.id = :userId")
  List<ReadStatus> findAllByUserIdWithUser(@Param("userId") UUID userId);

  // N+1 문제 해결: 단일 ReadStatus 조회 시 연관 엔티티를 함께 조회
  @Query("SELECT rs FROM ReadStatus rs " +
      "JOIN FETCH rs.user " +
      "JOIN FETCH rs.channel " +
      "WHERE rs.id = :readStatusId")
  Optional<ReadStatus> findByIdWithUserAndChannel(@Param("readStatusId") UUID readStatusId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  // N+1 문제 해결: ReadStatus 조회 시 사용자 정보를 함께 조회
  @Query("SELECT rs FROM ReadStatus rs " +
      "JOIN FETCH rs.user " +
      "WHERE rs.channel.id = :channelId")
  List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);

  // N+1 문제 해결: 중복 체크를 위한 존재 여부 확인 쿼리
  @Query("SELECT COUNT(rs) > 0 FROM ReadStatus rs WHERE rs.user.id = :userId AND rs.channel.id = :channelId")
  boolean existsByUserIdAndChannelId(@Param("userId") UUID userId, @Param("channelId") UUID channelId);

  // N+1 문제 해결: 사용자별 채널별 ReadStatus 조회
  @Query("SELECT rs FROM ReadStatus rs " +
      "JOIN FETCH rs.user " +
      "JOIN FETCH rs.channel " +
      "WHERE rs.user.id = :userId AND rs.channel.id = :channelId")
  Optional<ReadStatus> findByUserIdAndChannelIdWithUserAndChannel(@Param("userId") UUID userId,
      @Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
