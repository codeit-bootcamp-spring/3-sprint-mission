package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BinaryContentCreatedEvent event) {
        try {
            binaryContentStorage.put(event.id(), event.bytes());
            binaryContentService.updateStatus(
                    event.id(), BinaryContentStatus.SUCCESS
            );
        } catch (Exception e) {
            binaryContentService.updateStatus(
                    event.id(), BinaryContentStatus.FAIL
            );
        }
    }
}