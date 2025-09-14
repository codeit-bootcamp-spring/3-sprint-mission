package com.sprint.mission.discodeit.web.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 사용자별 SSE 연결(SseEmitter) 목록을 관리하는 인메모리 저장소.
 *
 * <p>동시성 안전을 위해 ConcurrentMap + CopyOnWriteArrayList를 사용한다.
 * 하나의 사용자(UUID) 당 다중 탭/다중 연결을 허용한다.</p>
 */
@Repository
public class SseEmitterRepository {

    // 사용자별 다중 탭 지원
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    /**
     * 지정한 사용자에 대한 SseEmitter를 등록한다.
     *
     * @param userId     등록 대상 사용자 ID
     * @param sseEmitter 등록할 SseEmitter
     * @return 등록 이후의 해당 사용자 연결 목록(참조)
     */
    public List<SseEmitter> add(UUID userId, SseEmitter sseEmitter) {
        List<SseEmitter> sseEmitters = data.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        sseEmitters.add(sseEmitter);
        return sseEmitters;
    }

    /**
     * 지정한 사용자의 모든 SseEmitter 목록을 조회한다.
     *
     * @param userId 조회 대상 사용자 ID
     * @return 연결 목록, 없으면 null
     */
    public List<SseEmitter> get(UUID userId) {
        return data.get(userId);
    }

    /**
     * 전체 사용자 → 연결 목록 맵을 반환한다.
     *
     * @return 전체 연결 맵(참조)
     */
    public Map<UUID, List<SseEmitter>> getAll() {
        return data;
    }

    /**
     * 지정한 사용자에서 특정 SseEmitter를 제거한다.
     * 연결 목록이 비면 사용자 엔트리를 삭제한다.
     *
     * @param userId     사용자 ID
     * @param sseEmitter 제거할 연결
     */
    public void remove(UUID userId, SseEmitter sseEmitter) {
        List<SseEmitter> sseEmitters = data.get(userId);
        if (sseEmitters == null) return;
        sseEmitters.remove(sseEmitter);
        if (sseEmitters.isEmpty()) data.remove(userId);
    }
}
