package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

  private static final long DEFAULT_TIMEOUT = 60 * 60 * 1000L;

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitterRepository.add(receiverId, emitter);

    emitter.onCompletion(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onError(e -> emitterRepository.remove(receiverId, emitter));

    ping(emitter);

    if (lastEventId != null) {
      Map<UUID, Object> missed = messageRepository.findAllAfter(lastEventId);
      missed.forEach((eventId, message) -> {
        try {
          emitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(getEventName(message))
              .data(message));
        } catch (IOException ex) {
          emitterRepository.remove(receiverId, emitter);
        }
      });
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    messageRepository.save(eventId, data);
    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = emitterRepository.get(receiverId);
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data));
        } catch (IOException ex) {
          emitterRepository.remove(receiverId, emitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    messageRepository.save(eventId, data);
    emitterRepository.getAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data));
        } catch (IOException ex) {
          emitterRepository.remove(receiverId, emitter);
        }
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    emitterRepository.getAll().forEach((receiverId, emitters) -> {
      emitters.removeIf(emitter -> !ping(emitter));
      if (emitters.isEmpty()) {
        emitterRepository.removeAll(receiverId);
      }
    });
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("ping").data("pong"));
      return true;
    } catch (IOException ex) {
      return false;
    }
  }

  private String getEventName(Object message) {
    // TODO: DTO 타입별로 이벤트명 매핑 필요
    return "unknown";
  }
}
