package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
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
public class BinaryContentCreatedEventListener {

    private final BinaryContentStorage storage;
    private final BinaryContentService binaryContentService;

    @Async("binaryContentExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {

        log.debug("[BinaryContentCreatedEventListener] onBinaryContent id: {}, fileName: {}",
            event.binaryContent().getId(),
            event.binaryContent().getFileName());

        UUID id = event.binaryContent().getId();
        try {
            storage.put(id, event.data());

            binaryContentService.updateStatus(id, BinaryContentStatus.SUCCESS);
            log.info("[BinaryContentCreatedEventListener] 바이너리 데이터 저장 성공: {}", id);
        } catch (Exception e) {
            log.error("[BinaryContentCreatedEventListener] 바이너리 데이터 저장 성공: {}, 실패 이유: {}", id,
                e.getMessage(), e);

            binaryContentService.updateStatus(id, BinaryContentStatus.FAIL);
        }
    }
}
