package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.UserUpdateEvent;
import com.sprint.mission.discodeit.service.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserUpdateEventListener {
    private final SseService sseService;

    @Async("userTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserUpdateEvent event) {
        String eventName = switch (event.type()) {
            case CREATED -> "users.created";
            case UPDATED -> "users.updated";
            case DELETED -> "users.deleted";
        };

        try {
            sseService.broadcast(eventName, event.dto());
            log.debug("[UserUpdateEventListener] {} 전송 성공: userId={}", eventName, event.dto().id());
        } catch (Exception e) {
            log.error("[UserUpdateEventListener] {} 전송 실패: userId={}", eventName, event.dto().id(), e);
        }
    }
}
