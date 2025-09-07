package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.response.SseMessage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage<?>> messages = new ConcurrentHashMap<>();

  public <T> void save(UUID eventId, String name, T data) {
    eventIdQueue.addLast(eventId);
    messages.put(eventId, new SseMessage<>(eventId, name, data));

    if (eventIdQueue.size() > 1000) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) {
        messages.remove(oldest);
      }
    }
  }

  public SseMessage<?> find(UUID eventId) {
    return messages.get(eventId);
  }

  public Map<UUID, SseMessage<?>> findAllAfter(UUID lastEventId) {
    Map<UUID, SseMessage<?>> result = new LinkedHashMap<>();
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
