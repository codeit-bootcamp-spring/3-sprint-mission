package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
    private static final int MAX_SIZE = 1000;

    public void save(SseMessage message) {
        messages.put(message.id(), message);
        eventIdQueue.addLast(message.id());

        // 큐 크기 제한
        while (eventIdQueue.size() > MAX_SIZE) {
            UUID oldestId = eventIdQueue.pollFirst();
            if (oldestId != null) {
                messages.remove(oldestId);
            }
        }

        log.debug("SSE 메시지 저장 : ID = {}, eventName = {} ", message.id(), message.eventName());
    }

    public List<SseMessage> findEventsAfter(UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();
        boolean found = false;

        for (UUID evnetId : eventIdQueue) {
            if (found) {
                SseMessage message = messages.get(evnetId);
                if (message != null) {
                    result.add(message);
                }
            } else if (evnetId.equals(lastEventId)) {
                found = true;
            }
        }

        if (lastEventId == null) {
            return messages.values().stream().toList();
        }

        return result;
    }
}