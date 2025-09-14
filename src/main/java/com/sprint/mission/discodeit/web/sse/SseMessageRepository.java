package com.sprint.mission.discodeit.web.sse;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * SSE 이벤트 유실 복구를 위한 메시지 버퍼 저장소.
 *
 * <p>최근 N개(MAX_BUFFER)의 이벤트를 보관하고, 재연결 시 클라이언트가 보낸
 * lastEventId 이후의 이벤트만 재전송할 수 있도록 조회 기능을 제공한다.</p>
 */
@Repository
public class SseMessageRepository {

    /**
     * 버퍼에 저장되는 SSE 메시지 레코드.
     *
     * @param id        고유 이벤트 ID(UUID)
     * @param eventName 이벤트 이름
     * @param data      페이로드(직렬화 가능한 객체)
     * @param at        생성 시각
     */
    public record SseMessage(UUID id, String eventName, Object data, Instant at) {}

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
    private static final int MAX_BUFFER = 200;

    /**
     * 새 메시지를 저장하고, 버퍼 초과 시 가장 오래된 메시지를 제거한다.
     *
     * @param eventName 이벤트 이름
     * @param data      전송 데이터
     * @return 생성된 이벤트 ID(UUID)
     */
    public UUID save(String eventName, Object data) {
        UUID id = UUID.randomUUID();
        SseMessage message = new SseMessage(id, eventName, data, Instant.now());
        messages.put(id, message);
        eventIdQueue.addLast(id);
        while (eventIdQueue.size() > MAX_BUFFER) {
            UUID old = eventIdQueue.pollFirst();
            if (old != null) messages.remove(old);
        }

        return id;
    }

    /**
     * 주어진 마지막 이벤트 ID 이후에 발생한 메시지 목록을 순서대로 반환한다.
     *
     * @param lastEventId 마지막으로 수신한 이벤트 ID
     * @return 이후 메시지 목록(없으면 빈 리스트)
     */
    public List<SseMessage> findAfter(UUID lastEventId) {
        if (lastEventId == null) return List.of();
        List<SseMessage> result = new ArrayList<>();
        boolean pass = false;
        for (UUID id : eventIdQueue) {
            if (!pass) {
                if (id.equals(lastEventId)) pass = true;
                continue;
            }
            SseMessage message = messages.get(id);
            if (message != null) result.add(message);
        }

        return result;
    }
}
