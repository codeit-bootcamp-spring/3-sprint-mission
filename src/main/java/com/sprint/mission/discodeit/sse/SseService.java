package com.sprint.mission.discodeit.sse;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private static final long DEFAULT_TIMEOUT_MS = 30L * 60L * 1000L; // 30분

    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository messageRepository;

    /** 최초 연결 & 유실 복원 */
    public SseEmitter connect(UUID receiverId, @Nullable UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT_MS);

        // 콜백 등록
        emitter.onCompletion(() -> emitterRepository.remove(receiverId, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(receiverId, emitter));
        emitter.onError((ex) -> emitterRepository.remove(receiverId, emitter));

        emitterRepository.add(receiverId, emitter);

        // 연결 확인용 ping
        ping(emitter);

        // 이벤트 유실 복원 (Last-Event-ID 이후)
        if (lastEventId != null) {
            var backlog = messageRepository.findAfter(lastEventId);
            for (SseMessage m : filterByReceiver(backlog, receiverId)) {
                safeSend(emitter, m.getId(), m.getName(), m.getData());
            }
        }

        return emitter;
    }

    /** 선택 사용자에게 이벤트 전송 */
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        if (receiverIds == null || receiverIds.isEmpty()) return;

        UUID eventId = UUID.randomUUID();
        SseMessage msg = new SseMessage(eventId, eventName, data, Set.copyOf(receiverIds), Instant.now());
        messageRepository.save(msg);

        for (UUID receiverId : receiverIds) {
            var emitters = new CopyOnWriteArrayList<>(emitterRepository.getAll(receiverId));
            for (SseEmitter emitter : emitters) {
                boolean ok = safeSend(emitter, eventId, eventName, data);
                if (!ok) emitterRepository.remove(receiverId, emitter);
            }
        }
    }

    /** 전체 브로드캐스트 */
    public void broadcast(String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage msg = new SseMessage(eventId, eventName, data, null, Instant.now());
        messageRepository.save(msg);

        for (var entry : emitterRepository.allEntries()) {
            UUID receiverId = entry.getKey();
            for (SseEmitter emitter : new CopyOnWriteArrayList<>(entry.getValue())) {
                boolean ok = safeSend(emitter, eventId, eventName, data);
                if (!ok) emitterRepository.remove(receiverId, emitter);
            }
        }
    }

    /** 30분마다 연결 상태 점검 & 정리 */
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        for (var entry : emitterRepository.allEntries()) {
            UUID receiverId = entry.getKey();
            for (SseEmitter emitter : new CopyOnWriteArrayList<>(entry.getValue())) {
                if (!ping(emitter)) {
                    emitterRepository.remove(receiverId, emitter);
                }
            }
        }
    }

    /** 연결/타임아웃 확인용 더미 이벤트 */
    public boolean ping(SseEmitter sseEmitter) {
        return safeSend(sseEmitter, UUID.randomUUID(), "sse.ping", "pong");
    }

    private boolean safeSend(SseEmitter emitter, UUID id, String name, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(id.toString())
                .name(name)
                .data(data));
            return true;
        } catch (IOException e) {
            try { emitter.completeWithError(e); } catch (Exception ignore) {}
            log.debug("SSE 전송 실패 → emitter 제거 대상: {}", e.getMessage());
            return false;
        }
    }

    private List<SseMessage> filterByReceiver(List<SseMessage> messages, UUID receiverId) {
        return messages.stream()
            .filter(m -> m.getReceiverIds() == null || m.getReceiverIds().contains(receiverId))
            .collect(Collectors.toList());
    }
}
