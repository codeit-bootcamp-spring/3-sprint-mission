package com.sprint.mission.discodeit.repository.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SseEmitterRepository {

    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public SseEmitter add(UUID receiverId, SseEmitter emitter) {
        data.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(emitter);
        return emitter;
    }

    public List<SseEmitter> get(UUID receiverId) {
        return data.getOrDefault(receiverId, Collections.emptyList());
    }

    public void remove(UUID receiverId, SseEmitter emitter) {
        List<SseEmitter> emitters = data.get(receiverId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                data.remove(receiverId);
            }
        }
    }

    public Collection<List<SseEmitter>> findAll() {
        return data.values();
    }
}