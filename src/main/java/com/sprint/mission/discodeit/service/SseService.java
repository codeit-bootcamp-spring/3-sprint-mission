package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.SseMessageDto;
import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private static final long TIMEOUT = 1000L * 60 * 60; // 1시간
    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository messageRepository;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitter.onCompletion(() -> emitterRepository.remove(receiverId, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(receiverId, emitter));
        emitter.onError((ex) -> emitterRepository.remove(receiverId, emitter));

        emitterRepository.add(receiverId, emitter);

        // 연결 직후 ping
        ping(emitter);

        // 유실된 이벤트 복원
        if (lastEventId != null) {
            SseMessageDto missed = messageRepository.find(lastEventId);
            if (missed != null) {
                sendToEmitter(emitter, missed);
            }
        }

        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        UUID eventId = messageRepository.save(eventName, data);

        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = new CopyOnWriteArrayList<>(emitterRepository.get(receiverId));
            emitters.forEach(emitter -> sendToEmitter(emitter, new SseMessageDto(eventId, eventName, data)));
        }
    }

    public void broadcast(String eventName, Object data) {
        UUID eventId = messageRepository.save(eventName, data);

        emitterRepository.findAll().forEach(list -> {
            list.forEach(emitter -> sendToEmitter(emitter, new SseMessageDto(eventId, eventName, data)));
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        emitterRepository.findAll().forEach(list -> {
            list.removeIf(emitter -> !ping(emitter));
        });
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void sendToEmitter(SseEmitter emitter, SseMessageDto message) {
        try {
            emitter.send(SseEmitter.event()
                .id(message.id().toString())
                .name(message.eventName())
                .data(message.data()));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}