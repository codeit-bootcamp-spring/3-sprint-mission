package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationSseEventListener {

    private final SseService sseService;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(/* NotificationCreatedEvent */ Object event) {

        UUID receiverId = /* ((NotificationCreatedEvent) event).getReceiverId() */ null;
        NotificationDto dto = /* ((NotificationCreatedEvent) event).getData() */ null;

        if (receiverId != null && dto != null) {
            sseService.send(Set.of(receiverId), "notifications.created", dto);
        }
    }
}
