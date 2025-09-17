package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

    private static final int MAX_MESSAGES = 1000;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public void save(SseMessage message) {
        UUID eventId = message.getId();
        eventIdQueue.addLast(eventId);
        messages.put(eventId, message);

        if (eventIdQueue.size() > MAX_MESSAGES) {
            UUID oldest = eventIdQueue.pollFirst();
            if (oldest != null) {
                messages.remove(oldest);
            }
        }
    }

    public List<SseMessage> findAfter(UUID lastEventId) {
        if (lastEventId == null) {
            return List.of();
        }
        List<SseMessage> result = new ArrayList<>();

        for (UUID id : eventIdQueue) {
            if (id.equals(lastEventId)) {
                boolean start = false;
                for (UUID laterId : eventIdQueue) {
                    if (start) {
                        result.add(messages.get(laterId));
                    }
                    if (laterId.equals(id)) {
                        start = true;
                    }
                }
                break;
            }
        }
        return result;
    }
}
