package com.sprint.mission.discodeit.repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, Object> messages = new ConcurrentHashMap<>(); // TODO: SseMessage 타입으로 변경

  public void save(UUID eventId, Object message) {
    eventIdQueue.addLast(eventId);
    messages.put(eventId, message);

    if (eventIdQueue.size() > 1000) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) {
        messages.remove(oldest);
      }
    }
  }

  public Object find(UUID eventId) {
    return messages.get(eventId);
  }

  public Map<UUID, Object> findAllAfter(UUID lastEventId) {
    Map<UUID, Object> result = new LinkedHashMap<>();
    boolean found = (lastEventId == null);
    for (UUID id : eventIdQueue) {
      if (!found && id.equals(lastEventId)) {
        found = true;
        continue;
      }
      if (found) {
        result.put(id, messages.get(id));
      }
    }
    return result;
  }

  public void remove(UUID eventId) {
    eventIdQueue.remove(eventId);
    messages.remove(eventId);
  }
}
