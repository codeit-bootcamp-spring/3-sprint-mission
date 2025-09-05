package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessageDto;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * PackageName  : com.sprint.mission.discodeit.repository
 * FileName     : SseMessageRepository
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */
@Repository
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessageDto> messages = new ConcurrentHashMap<>();
    private final int maxSize = 1000;

    public void store(SseMessageDto message) {
        UUID id = message.id();
        messages.put(id, message);
        eventIdQueue.addLast(id);
        trim();
    }

    private void trim() {
        while (eventIdQueue.size() > maxSize) {
            UUID old = eventIdQueue.pollFirst();
            if (old != null) messages.remove(old);
        }
    }

    public List<SseMessageDto> getAfter(UUID lastEventId, int limit) {
        if (eventIdQueue.isEmpty()) return List.of();
        List<SseMessageDto> result = new ArrayList<>();
        boolean take = (lastEventId == null);
        int count = 0;

        for (UUID id : eventIdQueue) {
            if (!take) {
                if (id.equals(lastEventId)) take = true;
                continue;
            }
            SseMessageDto m = messages.get(id);
            if (m != null) {
                result.add(m);
                if (++count >= limit) break;
            }
        }
        return result;
    }

    public List<SseMessageDto> getRecent(int limit) {
        List<SseMessageDto> list = new ArrayList<>();
        Iterator<UUID> it = eventIdQueue.descendingIterator();
        while (it.hasNext() && list.size() < limit) {
            UUID id = it.next();
            SseMessageDto m = messages.get(id);
            if (m != null) list.add(m);
        }
        Collections.reverse(list);
        return list;
    }
}
