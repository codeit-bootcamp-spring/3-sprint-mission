package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.SseMessageDto;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * PackageName  : com.sprint.mission.discodeit.service.basic
 * FileName     : BasicSseService
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {
    private static final long TIMEOUT_MS = TimeUnit.MINUTES.toMillis(30);

    private final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    private final SseEmitterRepository emitterRepo;
    private final SseMessageRepository messageRepo;

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emitterRepo.add(receiverId, emitter);

        emitter.onCompletion(() -> emitterRepo.remove(receiverId, emitter));
        emitter.onTimeout(() -> emitterRepo.remove(receiverId, emitter));
        emitter.onError(e -> emitterRepo.remove(receiverId, emitter));

        ping(emitter);
        tryReplay(emitter, lastEventId);

        return emitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        if (receiverIds == null || receiverIds.isEmpty()) return;

        SseMessageDto message = new SseMessageDto(UUID.randomUUID(), eventName, data, Instant.now());
        messageRepo.store(message);

        for (UUID receiverId : receiverIds) {
            for (SseEmitter emitter : emitterRepo.get(receiverId)) {
                if (!safeSend(emitter, message)) {
                    emitterRepo.remove(receiverId, emitter);
                }
            }
        }
    }

    @Override
    public void broadcast(String eventName, Object data) {
        SseMessageDto message = new SseMessageDto(UUID.randomUUID(), eventName, data, Instant.now());
        messageRepo.store(message);

        for (var entry : emitterRepo.snapshot().entrySet()) {
            UUID receiverId = entry.getKey();
            for (SseEmitter emitter : entry.getValue()) {
                if (!safeSend(emitter, message)) {
                    emitterRepo.remove(receiverId, emitter);
                }
            }
        }
    }

    @Override
    public void cleanUp() {
        for (var entry : emitterRepo.snapshot().entrySet()) {
            UUID receiverId = entry.getKey();
            for (SseEmitter emitter : entry.getValue()) {
                if (!ping(emitter)) {
                    emitterRepo.remove(receiverId, emitter);
                }
            }
        }
    }

    @Override
    public boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("ping").id(UUID.randomUUID().toString()).data("ok"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private void tryReplay(SseEmitter emitter, UUID lastEventId) {
        List<SseMessageDto> toReplay = (lastEventId != null)
            ? messageRepo.getAfter(lastEventId, 200)
            : messageRepo.getRecent(20);

        for (SseMessageDto m : toReplay) {
            if (!safeSend(emitter, m)) break;
        }
    }

    private boolean safeSend(SseEmitter emitter, SseMessageDto message) {
        try {
            emitter.send(SseEmitter.event()
                .name(message.eventName())
                .id(message.id().toString())
                .data(message.data()));
            return true;
        } catch (IOException e) {
            try { emitter.completeWithError(e); } catch (Exception ignore) {}
            return false;
        }
    }
}
