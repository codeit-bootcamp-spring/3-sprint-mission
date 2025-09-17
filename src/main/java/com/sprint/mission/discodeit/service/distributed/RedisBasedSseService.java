package com.sprint.mission.discodeit.service.distributed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.SseService;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 분산 환경용 Redis 기반 SSE 서비스 구현체
 * Redis Pub/Sub을 사용하여 다중 인스턴스 간 메시지 동기화
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.sse.type", havingValue = "redis", matchIfMissing = false)
public class RedisBasedSseService implements SseService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 로컬 SSE 연결만 관리 (각 인스턴스마다)
    private final ConcurrentMap<UUID, SseEmitter> localConnections = new ConcurrentHashMap<>();

    // Redis 채널명
    private static final String SSE_CHANNEL = "sse:broadcast";
    private static final String SSE_TARGETED_CHANNEL = "sse:targeted";
    private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30분

    @Override
    public SseEmitter connect(UUID userId, UUID lastEventId) {
        log.info("[SSE 연결] 사용자: {}, 인스턴스: {}", userId, getInstanceId());

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 로컬 연결 저장
        localConnections.put(userId, emitter);

        // 연결 해제 처리
        emitter.onCompletion(() -> {
            localConnections.remove(userId);
            log.info("[SSE 연결 해제] 사용자: {}", userId);
        });

        emitter.onTimeout(() -> {
            localConnections.remove(userId);
            log.info("[SSE 연결 타임아웃] 사용자: {}", userId);
        });

        emitter.onError(throwable -> {
            localConnections.remove(userId);
            log.error("[SSE 연결 에러] 사용자: {}", userId, throwable);
        });

        // 연결 성공 메시지
        try {
            emitter.send(SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name("connected")
                .data("SSE 연결 성공"));
        } catch (Exception e) {
            log.error("[SSE 연결 실패] 사용자: {}", userId, e);
            localConnections.remove(userId);
        }

        return emitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        log.info("[SSE 타겟 전송] 대상: {}, 이벤트: {}", receiverIds.size(), eventName);

        try {
            SseMessage message = new SseMessage(List.copyOf(receiverIds), eventName, data);

            // Redis Pub/Sub으로 메시지 발행 (모든 인스턴스에 전달)
            redisTemplate.convertAndSend(SSE_TARGETED_CHANNEL, message);

            log.debug("[Redis Pub/Sub 발행] 채널: {}, 대상: {} 명", SSE_TARGETED_CHANNEL, receiverIds.size());
        } catch (Exception e) {
            log.error("[SSE 타겟 전송 실패] 대상: {}, 이벤트: {}", receiverIds.size(), eventName, e);
        }
    }

    @Override
    public void broadcast(String eventName, Object data) {
        log.info("[SSE 브로드캐스트] 이벤트: {}", eventName);

        try {
            SseMessage message = new SseMessage(null, eventName, data);

            // Redis Pub/Sub으로 브로드캐스트 (모든 인스턴스에 전달)
            redisTemplate.convertAndSend(SSE_CHANNEL, message);

            log.debug("[Redis Pub/Sub 브로드캐스트] 채널: {}", SSE_CHANNEL);
        } catch (Exception e) {
            log.error("[SSE 브로드캐스트 실패] 이벤트: {}", eventName, e);
        }
    }

    // Redis에서 메시지를 받아 로컬 연결들에게 전송
    public void handleBroadcastMessage(SseMessage message) {
        log.debug("[로컬 브로드캐스트 처리] 연결 수: {}, 이벤트: {}",
            localConnections.size(), message.getEventName());

        localConnections.forEach((userId, emitter) -> {
            sendToEmitter(userId, emitter, message.getEventName(), message.getData());
        });
    }

    // Redis에서 메시지를 받아 특정 사용자들에게 전송
    public void handleTargetedMessage(SseMessage message) {
        List<UUID> targetUsers = message.getTargetUsers();
        if (targetUsers == null) return;

        log.debug("[로컬 타겟 전송 처리] 대상: {} 명, 이벤트: {}",
            targetUsers.size(), message.getEventName());

        targetUsers.forEach(userId -> {
            SseEmitter emitter = localConnections.get(userId);
            if (emitter != null) {
                sendToEmitter(userId, emitter, message.getEventName(), message.getData());
            }
        });
    }

    private void sendToEmitter(UUID userId, SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(eventName)
                .data(data));
        } catch (Exception e) {
            log.error("[SSE 개별 전송 실패] 사용자: {}, 이벤트: {}", userId, eventName, e);
            localConnections.remove(userId);
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10) // 10분마다
    public void cleanUp() {
        log.info("[SSE 연결 정리 시작] 연결 수: {}", localConnections.size());

        localConnections.entrySet().removeIf(entry -> {
            UUID userId = entry.getKey();
            SseEmitter emitter = entry.getValue();

            if (!ping(emitter)) {
                log.debug("[SSE 연결 제거] 사용자: {}", userId);
                return true;
            }
            return false;
        });

        log.info("[SSE 연결 정리 완료] 남은 연결 수: {}", localConnections.size());
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                .name("ping")
                .data("ping"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String getInstanceId() {
        return System.getProperty("instance.id", "unknown");
    }

    // SSE 메시지 래퍼 클래스
    public static class SseMessage {
        private List<UUID> targetUsers;
        private String eventName;
        private Object data;
        private Instant timestamp;
        private String instanceId;

        public SseMessage() {
            this.timestamp = Instant.now();
            this.instanceId = System.getProperty("instance.id", "unknown");
        }

        public SseMessage(List<UUID> targetUsers, String eventName, Object data) {
            this();
            this.targetUsers = targetUsers;
            this.eventName = eventName;
            this.data = data;
        }

        // Getters and Setters
        public List<UUID> getTargetUsers() { return targetUsers; }
        public void setTargetUsers(List<UUID> targetUsers) { this.targetUsers = targetUsers; }
        public String getEventName() { return eventName; }
        public void setEventName(String eventName) { this.eventName = eventName; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        public String getInstanceId() { return instanceId; }
        public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    }
}
