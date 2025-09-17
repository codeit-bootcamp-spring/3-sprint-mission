package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;


    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter sseEmitter = new SseEmitter(3600000L);

        sseEmitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
        sseEmitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
        sseEmitter.onError(e -> sseEmitterRepository.remove(receiverId, sseEmitter));

        sseEmitterRepository.save(receiverId, sseEmitter);
        if (lastEventId != null) {
            sseMessageRepository.getAfter(lastEventId).forEach(sseMessage->{
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name(sseMessage.getEventName())
                            .data(sseMessage.getData())
                            .id(sseMessage.getId().toString()));
                } catch (IOException e) {
                    log.error("Failed to send missed messages", e); // ✨ 로깅만 하고 연결 유지
                }
            });
        } else{
            try {
                sseEmitter.send(SseEmitter.event()
                    .name("ping")
                    .build());
            } catch (IOException e) {
                log.error("Failed to send missed messages", e); // ✨ 로깅만 하고 연결 유지
            }
        }
        return sseEmitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        receiverIds.forEach(receiverId -> {

            List<SseEmitter> load = sseEmitterRepository.load(receiverId);

            if (load != null && !load.isEmpty()) {
                 // 연결이 없으면 건너뛴다
                for(SseEmitter sseEmitter : load){
                    try {
                        sseEmitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(data)
                            .id(eventId.toString())
                            .build());
                    } catch (IOException e) {
                        log.error("Failed to send missed messages", e); // ✨ 로깅만 하고 연결 유지
                    }
                }
            }

        });
        sseMessageRepository.save(new SseMessage(eventId, eventName, data));

    }
    public void broadcast(String eventName, Object data) {
        send(sseEmitterRepository.findAll().keySet(), eventName, data);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        sseEmitterRepository.findAll().forEach((receiverId, sseEmitters) -> {
            sseEmitters.removeIf(sseEmitter -> !ping(sseEmitter));
        });

    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("ping")
                    .data("heartbeat"));
            return true;
        } catch (IOException e) {
            return false;
        }

    }
}