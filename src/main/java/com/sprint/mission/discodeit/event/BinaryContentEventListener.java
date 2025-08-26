// src/main/java/com/sprint/mission/discodeit/event/BinaryContentEventListener.java
package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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

    private final BinaryContentStorage storage;
    private final BinaryContentService binaryContentService;

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BinaryContentCreatedEvent event) {
        try {
            storage.put(event.binaryContentId(), event.bytes(), event.contentType());

            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
            log.info("[BinaryContent] 저장 성공: id={}", event.binaryContentId());
        } catch (Exception e) {

            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
            log.error("[BinaryContent] 저장 실패: id={}", event.binaryContentId(), e);
        }
    }
}