package com.sprint.mission.discodeit.repository;

import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void save(UUID receiverId, SseEmitter sseEmitter) {
        data.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>()).add(sseEmitter);
    }

    public List<SseEmitter> load(UUID receiverId){
        List<SseEmitter> sseEmitters = data.get(receiverId);
        return sseEmitters;
    }

    public void remove(UUID userId, SseEmitter emitter) {
        List<SseEmitter> emitters = data.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    public ConcurrentMap<UUID, List<SseEmitter>> findAll(){
        return data;
    }

}
