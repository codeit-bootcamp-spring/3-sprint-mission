package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void add(UUID userId, SseEmitter sseEmitter) {
        data.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(sseEmitter);
    }

    public List<SseEmitter> get(UUID userId) {
        return data.getOrDefault(userId, List.of());
    }

    public void remove(UUID userId, SseEmitter sseEmitter) {
        List<SseEmitter> sseEmitters = data.get(userId);
        if (sseEmitters != null) {
            sseEmitters.remove(sseEmitter);
        }
    }

    public Map<UUID, List<SseEmitter>> findAll() {
        return data;
    }
}
