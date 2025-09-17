package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 단일 인스턴스 환경용 SSE 서비스 구현체
 * Redis를 사용하지 않는 기본 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.sse.type", havingValue = "basic", matchIfMissing = false)
public class BasicSseService implements SseService {

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    // 타임아웃을 더 길게 설정
    private static final long DEFAULT_TIMEOUT = 120L * 1000 * 60;

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 연결 저장
        sseEmitterRepository.save(receiverId, sseEmitter);

        // 연결 완료/에러/타임아웃 시 정리
        sseEmitter.onCompletion(() -> {
            sseEmitterRepository.delete(receiverId, sseEmitter);
            log.debug("SSE 연결 완료됨 : receiverId = {}", receiverId);
        });
        sseEmitter.onTimeout(() -> {
            sseEmitterRepository.delete(receiverId, sseEmitter);
            log.debug("SSE 연결 타임아웃 : receiverId = {}", receiverId);
        });
        sseEmitter.onError(throwable -> {
            sseEmitterRepository.delete(receiverId, sseEmitter);
            log.debug("SSE 연결 에러 : receiverId = {}", receiverId);
        });

        // 초기 연결 확인을 위한 ping 전송
        try {
            sseEmitter.send(SseEmitter.event()
                .name("connected")
                .data("연결 성공"));
            log.info("SSE 연결 성공 : receiverId = {}", receiverId);
        } catch (IOException e) {
            log.error("SSE 초기 연결 실패 : receiverId = {}", receiverId);
            sseEmitterRepository.delete(receiverId, sseEmitter);
            return sseEmitter;
        }

        // 누락된 이벤트 재전송
        if (lastEventId != null) {
            List<SseMessage> missedMessages = sseMessageRepository.findEventsAfter(lastEventId);
            for (SseMessage message : missedMessages) {
                try {
                    sseEmitter.send(SseEmitter.event()
                        .id(message.id().toString())
                        .name(message.eventName())
                        .data(message.data()));
                } catch (IOException e) {
                    log.error("누락 메시지 전송 실패 : receiverId = {}, messageId = {}", receiverId, message.id());
                    break;
                }
            }
        }

        return sseEmitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage message = new SseMessage(eventId, eventName, data, Instant.now());

        // 메시지 저장
        sseMessageRepository.save(message);

        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                        .id(eventId.toString())
                        .name(eventName)
                        .data(data));
                    return false;
                } catch (IOException e) {
                    log.error("SSE 메시지 전송 실패 : receiverId = {}, eventName = {}", receiverId, eventName);
                    return true;
                }
            });
        }
        log.info("SSE 메시지 전송 완료 : eventName = {}, receiverIds = {}", eventName, receiverIds.size());
    }

    @Override
    public void broadcast(String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage message = new SseMessage(eventId, eventName, data, Instant.now());

        sseMessageRepository.save(message);

        sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                        .id(eventId.toString())
                        .name(eventName)
                        .data(data));
                    return false;
                } catch (IOException e) {
                    log.error("SSE 브로드캐스트 실패 : receiverId = {}", receiverId);
                    return true;
                }
            });
        });
        log.info("SSE 브로드캐스트 완료 : eventName = {}", eventName);
    }

    // ping 주기를 더 자주
    @Scheduled(fixedDelay = 1000 * 60 * 15)
    public void cleanUp() {
        log.debug("SSE 연결 정리 시작");

        sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
            emitters.removeIf(emitter -> !ping(emitter));
        });

        log.debug("SSE 연결 정리 완료");
    }

    // heartbeat를 더 자주 전송
    @Scheduled(fixedDelay = 1000 * 30) // 30초마다
    public void sendHeartbeat() {
        sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
            emitters.removeIf(emitter -> !sendHeartbeat(emitter));
        });
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                .name("ping")
                .data("ping"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean sendHeartbeat(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                .name("heartbeat")
                .data("하트"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}