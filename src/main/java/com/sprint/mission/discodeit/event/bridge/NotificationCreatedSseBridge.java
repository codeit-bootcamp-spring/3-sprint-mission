package com.sprint.mission.discodeit.event.bridge;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.event.bridge
 * FileName     : NotificationCreatedSseBridge
 * Author       : dounguk
 * Date         : 2025. 9. 4.
 */
@Component
@RequiredArgsConstructor
public class NotificationCreatedSseBridge {

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(NotificationCreatedEvent event) {
        NotificationDto dto = event.notification();
        UUID receiverId = dto.receiverId();
        sseService.send(List.of(receiverId), "notifications.created", dto);
    }
}