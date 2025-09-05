package com.sprint.mission.discodeit.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.service
 * FileName     : SseService
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */
public interface SseService {
    SseEmitter connect(UUID receiverId, UUID lastEventId);

    void send(Collection<UUID> receiverIds, String eventName, Object data);

    void broadcast(String eventName, Object data);

    void cleanUp();

    boolean ping(SseEmitter sseEmitter);
}
