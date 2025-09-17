package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentSseEventListener {

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(/* BinaryContentUpdatedEvent */ Object event) {
        UUID ownerId = /* ((BinaryContentUpdatedEvent) event).getOwnerId() */ null;
        BinaryContentDto dto = /* ((BinaryContentUpdatedEvent) event).getData() */ null;

        if (ownerId != null && dto != null) {
            sseService.send(Set.of(ownerId), "binaryContents.updated", dto);
        }
    }
}
