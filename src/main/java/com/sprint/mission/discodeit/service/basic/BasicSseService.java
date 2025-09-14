package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

    private static final long DEFAULT_TIMEOUT_MS = 60L * 60 * 1000; // 1시간

    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository messageRepository;

    /**
     * 새로운 SSE 연결 생성
     *
     * @param receiverId  수신자 UUID (사용자 ID)
     * @param lastEventId 마지막으로 수신한 이벤트 ID (재연결 시 유실 이벤트 복원에 사용)
     * @return 생성된 SseEmitter 객체
     */
    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {

        log.debug("[BasicSseService] SSE 연결 생성 시작 - receiverId: {} lastEventId: {}", receiverId,
            lastEventId);

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT_MS);
        emitterRepository.save(receiverId, sseEmitter);

        sseEmitter.onCompletion(() -> emitterRepository.remove(receiverId, sseEmitter));
        sseEmitter.onTimeout(() -> emitterRepository.remove(receiverId, sseEmitter));
        sseEmitter.onError(e -> emitterRepository.remove(receiverId, sseEmitter));

        // 연결 직후 ping 이벤트
        ping(sseEmitter);

        // 유실 이벤트 복원
        if (lastEventId != null) {
            List<SseMessage> missed = messageRepository.findAfter(lastEventId);
            for (SseMessage m : missed) {
                try {
                    sendOnEmitter(sseEmitter, m.getId(), m.getName(), m.getData());
                } catch (IOException ignored) {
                }
            }
        }

        log.debug("[BasicSseService] SSE 연결 생성 완료 - receiverId: {} lastEventId: {}", receiverId,
            lastEventId);

        return sseEmitter;
    }

    /**
     * 특정 사용자들에게 이벤트를 전송
     *
     * @param receiverIds 수신자 ID 목록
     * @param eventName   이벤트 이름
     * @param data        이벤트 데이터 (DTO 등)
     */
    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {

        log.debug("[BasicSseService] 사용자 {}명에게 이벤트 전송 시작- eventName: {}", receiverIds.size(),
            eventName);

        UUID id = UUID.randomUUID();
        SseMessage message = new SseMessage(id, eventName, data, Instant.now());
        messageRepository.save(message);

        for (UUID receiverId : receiverIds) {
            for (SseEmitter emitter : safeList(emitterRepository.get(receiverId))) {
                try {
                    sendOnEmitter(emitter, message.getId(), eventName, data);
                } catch (IOException e) {
                    emitterRepository.remove(receiverId, emitter);
                    tryClose(emitter);
                }
            }
        }

        log.debug("[BasicSseService] 사용자 {}명에게 이벤트 전송 성공- eventName: {}", receiverIds.size(),
            eventName);
    }

    /**
     * 모든 사용자에게 이벤트 전송
     *
     * @param eventName 이벤트 이름
     * @param data      이벤트 데이터
     */
    @Override
    public void broadcast(String eventName, Object data) {

        log.debug("[BasicSseService] 모든 사용자에게 이벤트 전송 - eventName: {}", eventName);
        log.debug("[BasicSseService] 연결된 SSE 클라이언트 수: {}", emitterRepository.size());

        UUID id = UUID.randomUUID();
        SseMessage message = new SseMessage(id, eventName, data, Instant.now());
        messageRepository.save(message);

        for (Map.Entry<UUID, List<SseEmitter>> entry : emitterRepository.getAll().entrySet()) {
            UUID userId = entry.getKey();
            for (SseEmitter emitter : safeList(entry.getValue())) {
                try {
                    sendOnEmitter(emitter, message.getId(), eventName, data);
                } catch (IOException e) {
                    emitterRepository.remove(userId, emitter);
                    tryClose(emitter);
                }
            }
        }

        log.debug("[BasicSseService] 이벤트 전송 성공 - eventName: {}", eventName);
    }

    /**
     * 주기적으로 연결 상태를 확인하고 만료된 Emitter와 오래된 메시지를 정리 30분마다 실행
     */
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    @Override
    public void cleanUp() {
        for (Map.Entry<UUID, List<SseEmitter>> entry : emitterRepository.getAll().entrySet()) {
            UUID userId = entry.getKey();
            for (SseEmitter emitter : safeList(entry.getValue())) {
                if (!ping(emitter)) {
                    emitterRepository.remove(userId, emitter);
                    tryClose(emitter);
                }
            }
        }
        messageRepository.cleanUpExpired();
    }

    /**
     * Emitter에 ping 이벤트를 보내 연결 상태를 확인
     *
     * @param sseEmitter 확인할 SseEmitter
     * @return 전송 성공 여부
     */
    private boolean ping(SseEmitter sseEmitter) {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .name("system.ping")
                .data("ok")
                .reconnectTime(3000);
            sseEmitter.send(event);

            log.debug("[BasicSseService] ping 이벤트 전송 성공");

            return true;
        } catch (IOException e) {
            log.warn("[BasicSseService] ping 이벤트 전송 실패");

            return false;
        }
    }

    private void sendOnEmitter(SseEmitter sseEmitter, UUID id, String eventName, Object data)
        throws IOException {

        SseEmitter.SseEventBuilder event = SseEmitter.event()
            .id(id.toString())
            .name(eventName)
            .data(data, MediaType.APPLICATION_JSON)
            .reconnectTime(3000);

        sseEmitter.send(event);
    }

    private void tryClose(SseEmitter sseEmitter) {
        try {
            sseEmitter.complete();
        } catch (Exception e) {
            log.warn("[BasicSseService] tryClose 중 예외 발생", e);
        }
    }

    private List<SseEmitter> safeList(List<SseEmitter> list) {
        if (list == null) {
            return List.of();
        }
        return (list instanceof CopyOnWriteArrayList) ? list : new CopyOnWriteArrayList<>(list);
    }
}
