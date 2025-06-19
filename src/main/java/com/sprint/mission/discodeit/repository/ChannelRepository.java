package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    // N+1 문제 해결: 채널과 참가자 정보를 Fetch Join으로 한 번에 조회
    @Query("SELECT DISTINCT c FROM Channel c " +
            "LEFT JOIN FETCH c.readStatuses rs " +
            "LEFT JOIN FETCH rs.user " +
            "WHERE c.id = :channelId")
    Optional<Channel> findByIdWithParticipants(@Param("channelId") UUID channelId);

    // N+1 문제 해결: 모든 채널과 참가자 정보를 Fetch Join으로 한 번에 조회
    @Query("SELECT DISTINCT c FROM Channel c " +
            "LEFT JOIN FETCH c.readStatuses rs " +
            "LEFT JOIN FETCH rs.user")
    List<Channel> findAllWithParticipants();

    // N+1 문제 해결: 특정 채널 ID 목록에 해당하는 채널들과 참가자 정보를 Fetch Join으로 조회
    @Query("SELECT DISTINCT c FROM Channel c " +
            "LEFT JOIN FETCH c.readStatuses rs " +
            "LEFT JOIN FETCH rs.user " +
            "WHERE c.id IN :channelIds")
    List<Channel> findAllByIdInWithParticipants(@Param("channelIds") List<UUID> channelIds);

    // N+1 문제 해결: 모든 채널과 참가자 정보를 조회하되, 메시지는 제외 (마지막 메시지 시간은 별도 조회)
    @Query("SELECT DISTINCT c FROM Channel c " +
            "LEFT JOIN FETCH c.readStatuses rs " +
            "LEFT JOIN FETCH rs.user")
    List<Channel> findAllWithParticipantsOnly();

    // 성능 최적화: 사용자가 접근 가능한 채널만 DB에서 필터링하여 조회
    // 공개 채널 + 사용자가 구독한 비공개 채널만 조회
    @Query("SELECT DISTINCT c FROM Channel c " +
            "LEFT JOIN FETCH c.readStatuses rs " +
            "LEFT JOIN FETCH rs.user " +
            "WHERE c.type = 'PUBLIC' " +
            "   OR c.id IN :subscribedChannelIds")
    List<Channel> findAccessibleChannelsWithParticipants(
            @Param("subscribedChannelIds") List<UUID> subscribedChannelIds);

    // 성능 최적화: 빈 구독 목록 처리를 위한 공개 채널만 조회 메서드
    @Query("SELECT DISTINCT c FROM Channel c " +
            "LEFT JOIN FETCH c.readStatuses rs " +
            "LEFT JOIN FETCH rs.user " +
            "WHERE c.type = 'PUBLIC'")
    List<Channel> findPublicChannelsWithParticipants();
}
