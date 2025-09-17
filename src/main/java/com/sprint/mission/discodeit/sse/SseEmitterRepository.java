package com.sprint.mission.discodeit.sse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

    // 사용자별 다중 탭/기기 연결 허용
    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> data = new ConcurrentHashMap<>();

    public void add(UUID receiverId, SseEmitter emitter) {
        data.computeIfAbsent(receiverId, id -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public void remove(UUID receiverId, SseEmitter emitter) {
        var list = data.get(receiverId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) data.remove(receiverId);
        }
    }

    public List<SseEmitter> getAll(UUID receiverId) {
        return data.getOrDefault(receiverId, new CopyOnWriteArrayList<>());
    }

    public Set<Map.Entry<UUID, CopyOnWriteArrayList<SseEmitter>>> allEntries() {
        return data.entrySet();
    }
}
