package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.dto.data.SseMessageDto;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessageDto> messages = new ConcurrentHashMap<>();
    private static final int MAX_SIZE = 1000; // 최근 1000개만 저장

    public UUID save(String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessageDto message = new SseMessageDto(eventId, eventName, data);

        eventIdQueue.addLast(eventId);
        messages.put(eventId, message);

        if (eventIdQueue.size() > MAX_SIZE) {
            UUID old = eventIdQueue.pollFirst();
            if (old != null) {
                messages.remove(old);
            }
        }
        return eventId;
    }

    public SseMessageDto find(UUID eventId) {
        return messages.get(eventId);
    }
}