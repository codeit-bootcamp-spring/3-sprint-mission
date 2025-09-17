package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@Slf4j
public class SseEmitterRepository {

    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void save(UUID receiverId, SseEmitter sseEmitter) {
        data.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(sseEmitter);
        log.info("SSE 연결 저장 : receiverId = {}, 현재 연결 수 = {} ", receiverId, data.get(receiverId).size());
    }

    public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
        return data.getOrDefault(receiverId, new ArrayList<>());
    }

    public void delete(UUID receiverId, SseEmitter sseEmitter) {
        List<SseEmitter> emitters = data.get(receiverId);
        if (emitters != null) {
            emitters.remove(sseEmitter);
            if (emitters.isEmpty()) {
                data.remove(receiverId);
            }
        }
    }

    public ConcurrentMap<UUID, List<SseEmitter>> findAll() {
        return data;
    }
}