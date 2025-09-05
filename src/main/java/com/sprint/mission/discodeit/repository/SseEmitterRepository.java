package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * PackageName  : com.sprint.mission.discodeit.repository
 * FileName     : SseEmitterRepository
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */
@Repository
public class SseEmitterRepository {

    ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>> data = new ConcurrentHashMap<>();

    public void add(UUID receiverId, SseEmitter emitter) {
        data.computeIfAbsent(receiverId, id -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public List<SseEmitter> get(UUID receiverId) {
        return data.getOrDefault(receiverId, new CopyOnWriteArrayList<>());
    }

    public void remove(UUID receiverId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = data.get(receiverId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) data.remove(receiverId);
        }
    }

    public Map<UUID, List<SseEmitter>> snapshot() {
        return data.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
    }
}
