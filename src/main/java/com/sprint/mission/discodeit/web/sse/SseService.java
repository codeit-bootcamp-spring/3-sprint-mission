package com.sprint.mission.discodeit.web.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 서버-발송형 이벤트(Server-Sent Events, SSE) 전송을 담당하는 서비스.
 *
 * <p>
 * - 사용자별 연결(SseEmitter) 관리는 {@link SseEmitterRepository}
 * - 이벤트 유실 복구(Last-Event-ID)는 {@link SseMessageRepository} 버퍼로 지원합니다.
 * - connect/send/broadcast/cleanUp/ping API를 통해 연결 수립, 전송, 정리, 연결 확인을 수행합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SseService {

    private static final long RECONNECT_MS = 3000;

    private final SseEmitterRepository emitterRepo;
    private final SseMessageRepository messageRepo;

    /**
     * 클라이언트의 SSE 연결을 수립합니다.
     *
     * <p>lastEventId가 주어지면 해당 ID 이후의 버퍼 메시지를 재전송하여 유실 이벤트를 복구합니다.
     * 연결 직후 ping 이벤트를 내려 연결 상태를 확정합니다.</p>
     *
     * @param receiverId  연결할 사용자 ID
     * @param lastEventId 마지막으로 수신한 이벤트 ID(없으면 null)
     * @return 생성된 SseEmitter
     */
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {

        SseEmitter emitter = new SseEmitter(1000L * 60 * 30); // 30m
        emitterRepo.add(receiverId, emitter);

        emitter.onCompletion(() -> emitterRepo.remove(receiverId, emitter));
        emitter.onTimeout(() -> emitterRepo.remove(receiverId, emitter));
        emitter.onError(e -> emitterRepo.remove(receiverId, emitter));

        // 유실된 메시지 복구: LastEventId 이후 메시지 재전송
        if (lastEventId != null) {
            for (var m : messageRepo.findAfter(lastEventId))  {
                try {
                    emitter.send(SseEmitter.event()
                            .name(m.eventName())
                            .data(m.data(), MediaType.APPLICATION_JSON)
                            .id(m.id().toString())
                            .reconnectTime(RECONNECT_MS));
                } catch (IOException ignored) { }
            }
        }

        // 최초 연결 또는 재연결 시 ping
        ping(emitter);

        return emitter;
    }

    /**
     * 지정한 사용자 집합에게 이벤트를 전송합니다.
     *
     * <p>전송 전에 메시지를 버퍼에 저장하고, 저장된 이벤트의 ID를 SSE 이벤트 id로 사용합니다.
     * 재연결 시 Last-Event-ID를 이용해 정확한 복구가 가능해집니다.</p>
     *
     * @param receiverIds 수신자 사용자 ID 컬렉션
     * @param eventName   이벤트 이름
     * @param data        전송할 데이터(직렬화 가능)
     */
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        if (receiverIds == null || receiverIds.isEmpty()) return;
        UUID id = messageRepo.save(eventName, data);

        for (UUID userId : receiverIds) {
            List<SseEmitter> emitters = emitterRepo.get(userId);
            if (emitters == null) continue;
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(data, MediaType.APPLICATION_JSON)
                            .id(id.toString())
                            .reconnectTime(RECONNECT_MS));
                } catch (IOException e) {
                    emitterRepo.remove(userId, emitter);
                }
            }
        }
    }

    /**
     * 모든 활성 사용자에게 브로드캐스트로 이벤트를 전송합니다.
     *
     * <p>버퍼에 저장된 이벤트의 ID를 SSE 이벤트 id로 사용합니다.</p>
     *
     * @param eventName 이벤트 이름
     * @param data      전송할 데이터
     */
    public void broadcast(String eventName, Object data) {
        UUID id = messageRepo.save(eventName, data);
        for (Map.Entry<UUID, List<SseEmitter>> entry : emitterRepo.getAll().entrySet()) {
            UUID userId = entry.getKey();
            for (SseEmitter emitter : entry.getValue()) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(data, MediaType.APPLICATION_JSON)
                            .id(id.toString())
                            .reconnectTime(RECONNECT_MS));
                } catch (IOException e) {
                    emitterRepo.remove(userId, emitter);
                }
            }
        }
    }

    /**
     * 주기적으로 ping을 보내 끊어진 연결을 정리합니다.
     *
     * <p>전송 실패하는 SseEmitter는 제거되며, 사용자별 연결이 모두 사라지면
     * 사용자 엔트리도 함께 제거합니다.</p>
     */
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        for (Map.Entry<UUID, List<SseEmitter>> entry : emitterRepo.getAll().entrySet()) {
            UUID userId = entry.getKey();
            List<SseEmitter> emitters = entry.getValue();
            emitters.removeIf(emitter -> !ping(emitter));
            if (emitters.isEmpty()) emitterRepo.getAll().remove(userId);
        }
    }

    /**
     * 연결 확인 및 재연결 유도용 더미 이벤트를 전송합니다.
     *
     * @param emitter 대상 SseEmitter
     * @return 전송 성공 시 true, 실패 시 false
     */
    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                    .name("ping")
                    .data(Instant.now().toString())
                    .id(UUID.randomUUID().toString())
                    .reconnectTime(RECONNECT_MS));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
