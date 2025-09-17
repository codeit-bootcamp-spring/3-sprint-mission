package com.sprint.mission.discodeit.sse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

    // 이벤트 재전송(유실 복원)용 - 최근 N개만 보존
    private static final int CAPACITY = 5000;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public void save(SseMessage message) {
        messages.put(message.getId(), message);
        eventIdQueue.addLast(message.getId());
        while (eventIdQueue.size() > CAPACITY) {
            UUID oldest = eventIdQueue.pollFirst();
            if (oldest != null) messages.remove(oldest);
        }
    }

    public Optional<SseMessage> findById(UUID id) {
        return Optional.ofNullable(messages.get(id));
    }

    /** lastEventId 이후의 메시지를 순서대로 반환 */
    public List<SseMessage> findAfter(UUID lastEventId) {
        if (lastEventId == null) return List.of();
        List<SseMessage> result = new ArrayList<>();
        boolean after = false;
        for (UUID id : eventIdQueue) {
            if (!after) {
                if (id.equals(lastEventId)) after = true;
                continue;
            }
            SseMessage msg = messages.get(id);
            if (msg != null) result.add(msg);
        }
        return result;
    }
}
