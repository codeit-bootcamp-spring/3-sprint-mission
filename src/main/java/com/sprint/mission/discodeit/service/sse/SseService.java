package com.sprint.mission.discodeit.service.sse;

import com.sprint.mission.discodeit.entity.SseMessage;
import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 사용자별 SseEmitter 객체를 생성하고 메시지를 전송하는 컴포넌트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    // 사용자별 SseEmitter 객체를 생성
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        // 1시간 = 3600000ms
        SseEmitter emitter = new SseEmitter(3600000L);
        log.debug("[SseService] SseEmitter 객체 생성 완료 - userId: {}", receiverId);

        // 저장
        sseEmitterRepository.save(receiverId, emitter);
        log.info("[SseService] SSE 클라이언트 등록 완료 - userId: {}", receiverId);

        // 연결 종료 시 정리 로직 설정
        setupEmitterCallbacks(receiverId, emitter);

        // 초기 연결 확인 메시지 전송
        if (!ping(emitter)) {
            sseEmitterRepository.delete(receiverId, emitter);
            log.warn("[SseService] 초기 ping 실패 - 제거: userId: {}", receiverId);
        }

        // 유실 복원
        int sent = sseMessageRepository.send(receiverId, lastEventId, emitter);
        log.info("[SseService] 유실 복원한 메시지: {}개, 유저: {}", sent, receiverId);

        log.info("[SseService] SSE 구독 완료 - userId: {}", receiverId);
        return emitter;
    }

    // SseEmitter 객체를 통해 이벤트를 전송 (일부 사용자들에게)
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }
        SseMessage sseMessage = sseMessageRepository.saveEvent(receiverIds, eventName, data);

        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllByUserId(receiverId);

            if (emitters.isEmpty()) {
                log.debug("[SseService] send - 연결 없음: userId={}", receiverId);
                continue;
            }
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(
                        SseEmitter.event()
                            .id(sseMessage.id().toString())
                            .name(sseMessage.eventName())
                            .data(sseMessage.data())
                    );
                    log.debug("[SseService] send - 전송 성공: userId: {}, event: {}", receiverId, eventName);

                } catch (IOException e) {
                    log.error("[SseService] send - 전송 실패, 연결 정리: userId: {}, event: {}", receiverId, eventName);
                    sseEmitterRepository.delete(receiverId, emitter);

                    try {
                        emitter.completeWithError(e);
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    // SseEmitter 객체를 통해 이벤트를 전송
    public void broadcast(String eventName, Object data) {

        // 브로드캐스트로 저장
        SseMessage sseMessage = sseMessageRepository.appendBroadcast(eventName, data);

        for (UUID userId : sseEmitterRepository.userIds()) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllByUserId(userId);

            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(
                        SseEmitter.event()
                            .id(sseMessage.id().toString())
                            .name(sseMessage.eventName())
                            .data(sseMessage.data())
                    );
                    log.debug("[SseService] broadcast - 전송 성공: userId: {}, event: {}", userId, eventName);

                } catch (IOException e) {
                    log.error("[SseService] broadcast - 전송 실패, 연결 정리: userId: {}, event: {}", userId, eventName);
                    sseEmitterRepository.delete(userId, emitter);

                    try {
                        emitter.completeWithError(e);
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    // 주기적으로 ping을 보내서 만료된 SseEmitter 객체를 삭제
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        for (UUID userId : sseEmitterRepository.userIds()) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllByUserId(userId);

            for (SseEmitter emitter : emitters) {
                if (!ping(emitter)) {
                    sseEmitterRepository.delete(userId, emitter);
                    log.debug("[SseService] cleanUp - 연결이 끊긴 사용자 SseEmitter 삭제 : userId: {}", userId);
                }
            }
        }
    }

    // 최초 연결 또는 만료 여부를 확인하기 위한 용도로 더미 이벤트를 전송
    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(
                SseEmitter.event()
                    .name("ping")
                    .data("keepAlive")
            );
            log.info("[SseService] 최초 연결 또는 만료 여부 확인을 위한 더미 이벤트 전송");
            return true;
        } catch (IOException e) {
            try {
                sseEmitter.completeWithError(e);
            } catch (Exception ignored) {}
            log.info("[SseService] 최초 연결 또는 만료 여부 확인을 위한 더미 이벤트 전송 실패 -  error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * SseEmitter의 콜백 메서드들을 설정한다
     * 연결 종료, 타임아웃, 오류 발생 시 자동 정리를 수행한다
     */
    private void setupEmitterCallbacks(UUID userId, SseEmitter emitter) {

        // 연결 정상 완료 시
        emitter.onCompletion(() -> {
            log.info("[SseService] SSE 연결 정상 종료 - userId: {}", userId);
            sseEmitterRepository.delete(userId, emitter);
        });

        // 연결 타임아웃 시
        emitter.onTimeout(() -> {
            log.warn("[SseService] SSE 연결 타임아웃 - userId: {}", userId);
            sseEmitterRepository.delete(userId, emitter);
        });

        // 연결 오류 시
        emitter.onError(throwable -> {
            log.error("[SseService] SSE 연결 오류 발생 - userId: {}, error: {}",
                userId, throwable.getMessage());
            sseEmitterRepository.delete(userId, emitter);
        });
    }
}
