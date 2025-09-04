package com.sprint.mission.discodeit.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

    private final ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>> data = new ConcurrentHashMap<>();

    public void save(UUID receiverId, SseEmitter sseEmitter) {
        data.computeIfAbsent(receiverId, key -> new CopyOnWriteArrayList<>())
            .add(sseEmitter);
    }

    public void remove(UUID receiverId, SseEmitter sseEmitter) {
        List<SseEmitter> emitters = data.get(receiverId);

        if (emitters != null) {
            emitters.remove(sseEmitter);
            if (emitters.isEmpty()) {
                data.remove(receiverId);
            }
        }
    }

    public List<SseEmitter> get(UUID userId) {
        return data.getOrDefault(userId, new CopyOnWriteArrayList<>());
    }

    public Map<UUID, List<SseEmitter>> getAll() {
        return Collections.unmodifiableMap(data);
    }

    public int size() {
        return data.size();
    }
}
