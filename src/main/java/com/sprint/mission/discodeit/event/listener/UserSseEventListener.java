package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.sse.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
public class UserSseEventListener {

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(/* UserCreatedEvent */ Object event) {
        UserDto dto = /* ((UserCreatedEvent) event).getData() */ null;
        if (dto != null) sseService.broadcast("users.created", dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(/* UserUpdatedEvent */ Object event) {
        UserDto dto = /* ((UserUpdatedEvent) event).getData() */ null;
        if (dto != null) sseService.broadcast("users.updated", dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(/* UserDeletedEvent */ Object event) {
        UserDto dto = /* ((UserDeletedEvent) event).getData() */ null;
        if (dto != null) sseService.broadcast("users.deleted", dto);
    }
}