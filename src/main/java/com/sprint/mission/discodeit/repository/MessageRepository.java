package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    // 채널 ID와 생성일 기준으로 이전 메시지 조회 (페이징)
    Slice<Message> findByChannelIdAndCreatedAtBefore(UUID channelId, Instant createdAt,
        Pageable pageable);

    // 채널 내 가장 최근 메시지 1건 조회
    Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

    // 위의 메서드 기반으로 최근 메시지 시간만 반환하는 헬퍼 메서드
    default Optional<Instant> findLastMessageAtByChannelId(UUID channelId) {
        return findTopByChannelIdOrderByCreatedAtDesc(channelId)
            .map(Message::getCreatedAt);
    }

    // 채널 ID 기준 메시지 전체 삭제
    void deleteAllByChannelId(UUID channelId);
}