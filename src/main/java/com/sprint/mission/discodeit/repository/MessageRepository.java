package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {


    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    @Query("SELECT m FROM Message m " +
            // message 엔티티를 조회하라 (별칭 m 사용)

            "LEFT JOIN FETCH m.author a " +
            "JOIN FETCH a.status " +
            "LEFT JOIN FETCH a.profile " +
            // message와 연관된 author 엔티티를 LEFT JOIN FETCH로 함께 가져오라
            // -> N+1 문제 방지 (author를 지연로딩 대신 즉시 가져오도록)

            "WHERE m.channel.id = :channelId AND m.createdAt < :createdAt")
            // message의 channel의 id가 파라미터 channelId와 같은 데이터를 조회하라
    Slice<Message> findAllByChannelIdWithAuthor(
            @Param("channelId") UUID channelId,
            @Param("createdAt") Instant createdAt,
            Pageable pageable);

    Optional<Message> findTop1ByChannelIdOrderByCreatedAtDesc(UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
