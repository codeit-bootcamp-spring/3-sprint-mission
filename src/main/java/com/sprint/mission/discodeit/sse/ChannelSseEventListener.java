package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(/* ChannelCreatedEvent */ Object event) {
        ChannelDto dto = /* ((ChannelCreatedEvent) event).getData() */ null;
        if (dto != null) sseService.broadcast("channels.created", dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(/* ChannelUpdatedEvent */ Object event) {
        ChannelDto dto = /* ((ChannelUpdatedEvent) event).getData() */ null;
        if (dto != null) sseService.broadcast("channels.updated", dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(/* ChannelDeletedEvent */ Object event) {
        ChannelDto dto = /* ((ChannelDeletedEvent) event).getData() */ null;
        if (dto != null) sseService.broadcast("channels.deleted", dto);
    }
}
