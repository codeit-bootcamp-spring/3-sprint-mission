package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.SseMessage;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicSseService implements SseService {

    private static final long TIMEOUT = 1000L * 60 * 60;

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;
    private final UserRepository userRepository;

    @Override
    public SseEmitter connect(UUID userId, UUID lastEventId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        SseEmitter sseEmitter = new SseEmitter(TIMEOUT);
        log.info("[SSE] 연결 등록 - userId={}, emitter={}", userId, sseEmitter);
        sseEmitterRepository.add(userId, sseEmitter);

        sseEmitter.onCompletion(() -> {
            sseEmitterRepository.remove(userId, sseEmitter);
            log.info("[SSE] 연결 종료 - userId={}", userId);
        });
        sseEmitter.onTimeout(() -> {
            sseEmitterRepository.remove(userId, sseEmitter);
            log.info("[SSE] 연결 타임아웃 - userId={}", userId);
        });
        sseEmitter.onError(e -> {
            sseEmitterRepository.remove(userId, sseEmitter);
            log.warn("[SSE] 연결 에러 - userId={}, error={}", userId, e.getMessage());
        });

        // 더미 이벤트 (ping)
        try {
            sendToEmitter(sseEmitter, "connect", "connected");
            log.info("[SSE] connect 이벤트 전송 완료 - userId={}", userId);
        } catch (Exception e) {
            log.warn("[SSE] connect 이벤트 전송 실패 - userId={}, error={}", userId, e.getMessage());
        }

        // 이벤트 유실 복원
        if (lastEventId != null) {
            List<SseMessage> missed = sseMessageRepository.findAfter(lastEventId);
            log.info("[SSE] 유실 이벤트 복원 - userId={}, lastEventId={}, 복원 개수={}",
                userId, lastEventId, missed.size());
            missed.forEach(
                m -> sendToEmitter(sseEmitter, m.getEventName(), m.getData(), m.getId()));
        }

        return sseEmitter;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    @Override
    public void cleanUp() {
        sseEmitterRepository.findAll().forEach((userId, emitters) -> {
            int before = emitters.size();
            emitters.removeIf(e -> !ping(e));
            int after = emitters.size();
            if (before != after) {
                log.info("[SSE] 만료 emitter 정리 - userId={}, before={}, after={}", userId, before,
                    after);
            }
        });
    }

    @Override
    public void broadcast(String eventName, Object data) {
        SseMessage message = new SseMessage(eventName, data);
        sseMessageRepository.save(message);
        log.info("[SSE] Broadcast 이벤트 전송 - event={}, id={}", eventName, message.getId());

        sseEmitterRepository.findAll().forEach((userId, emitters) -> {
            log.debug("[SSE] Broadcast → userId={}, targets={}", userId, emitters.size());
            emitters.forEach(e -> sendToEmitter(e, eventName, data, message.getId()));
        });
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        SseMessage message = new SseMessage(eventName, data);
        sseMessageRepository.save(message);
        log.info("[SSE] 개별 전송 이벤트 - event={}, id={}, targets={}",
            eventName, message.getId(), receiverIds.size());

        for (UUID userId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.get(userId);
            log.debug("[SSE] Send → userId={}, emitters={}", userId, emitters.size());
            emitters.forEach(e -> sendToEmitter(e, eventName, data, message.getId()));
        }
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void sendToEmitter(SseEmitter emitter, String eventName, Object data) {
        sendToEmitter(emitter, eventName, data, UUID.randomUUID());
    }

    private void sendToEmitter(SseEmitter emitter, String event, Object data, UUID eventId) {
        try {
            emitter.send(SseEmitter.event()
                .id(eventId.toString())
                .name(event)
                .data(data, MediaType.APPLICATION_JSON));
            log.debug("[SSE] 이벤트 전송 성공 - event={}, id={}", event, eventId);
        } catch (Exception e) {
            emitter.completeWithError(e);
            log.warn("[SSE] 이벤트 전송 실패 - event={}, id={}, error={}", event, eventId, e.getMessage());
        }
    }
}
