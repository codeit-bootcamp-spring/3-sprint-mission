package com.sprint.mission.discodeit.repository.sse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SseEmitter 객체를 메모리에서 저장하는 컴포넌트
 */
@Repository
public class SseEmitterRepository {

    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    // emitter 저장
    public void save(UUID userId, SseEmitter emitter) {
        data.compute(userId, (key, list) -> {
            if (list == null) {
                list = Collections.synchronizedList(new ArrayList<>());
            }
            list.add(emitter);
            return list;
        });
    }

    // emitter 조회
    public List<SseEmitter> findAllByUserId (UUID userId) {
        List<SseEmitter> list = data.get(userId);
        if (list == null) {
            return List.of();
        }
        synchronized (list) {
            return List.copyOf(list);
        }
    }

    // emitter 제거
    public void delete(UUID userId, SseEmitter emitter) {
        List<SseEmitter> list = data.get(userId);
        if(list == null) {
            return;
        }
        synchronized (list) {
            list.remove(emitter);
            if (list.isEmpty()) {
                data.remove(userId, list);
            }
        }
    }

    // 특정 유저의 emitter 제거
    public void deleteAllByUserId(UUID userId) {
        data.remove(userId);
    }

     // 현재 연결된 모든 사용자 ID
    public Set<UUID> userIds() {
        return Set.copyOf(data.keySet());
    }

}
