package com.sprint.mission.discodeit.event.listener.local;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BinaryContentCreatedEvent event) {

        UUID binaryContentId = event.binaryContentId();
        byte[] bytes = event.bytes();

        log.debug("[BinaryContentCreatedEventListener] AFTER_COMMIT - store id={} size={}",
            binaryContentId, bytes == null ? 0 : bytes.length);

        try {
            binaryContentStorage.put(binaryContentId, bytes);
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);

            log.debug("[BinaryContentCreatedEventListener] store complete -> SUCCESS");
        } catch (Exception e) {
            log.warn("[BinaryContentCreatedEventListener] store failed -> FAIL id={} cause={}",
                binaryContentId, e.toString());

            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
        }
    }
}
