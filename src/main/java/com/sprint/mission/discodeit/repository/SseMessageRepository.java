package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.SseMessage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    /**
     * 최대 보관 개수(초과 시 오래된 것부터 제거)
     */
    private static final int MAX_SIZE = 5_000;

    /**
     * TTL(기본 24시간) 초과 메시지 제거
     */
    private static final Duration TTL = Duration.ofHours(24);

    public SseMessage save(SseMessage message) {
        UUID id = message.getId();

        messages.put(id, message);
        eventIdQueue.addLast(id);
        trimBySize();

        return message;
    }

    public List<SseMessage> findAfter(UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();
        boolean collect = (lastEventId == null);

        for (UUID id : eventIdQueue) {
            if (!collect) {
                if (id.equals(lastEventId)) {
                    collect = true;
                }
                continue;
            }
            SseMessage msg = messages.get(id);
            if (msg != null && !isExpired(msg)) {
                result.add(msg);
            }
        }
        return result;
    }

    public void cleanUpExpired() {
        Iterator<UUID> it = eventIdQueue.iterator();
        while (it.hasNext()) {
            UUID id = it.next();
            SseMessage msg = messages.get(id);
            if (msg == null || isExpired(msg)) {
                it.remove();
                messages.remove(id);
            } else {
                break;
            }
        }
        trimBySize();
    }

    private boolean isExpired(SseMessage msg) {
        Instant created = msg.getCreatedAt();
        return created != null && created.plus(TTL).isBefore(Instant.now());
    }

    private void trimBySize() {
        while (eventIdQueue.size() > MAX_SIZE) {
            UUID oldest = eventIdQueue.pollFirst();
            if (oldest != null) {
                messages.remove(oldest);
            }
        }
    }
}
