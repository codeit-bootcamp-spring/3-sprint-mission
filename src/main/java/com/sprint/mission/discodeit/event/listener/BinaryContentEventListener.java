package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage storage;
    private final BinaryContentService binaryContentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(BinaryContentCreatedEvent e) {
        log.info("[Listener] received id={} size={}", e.contentId(), e.size());
        try {
            storage.put(e.contentId(), e.bytes());
            log.info("[Listener] storage.put OK id={}", e.contentId());
            binaryContentService.updateStatus(e.contentId(), BinaryContentStatus.SUCCESS);
            log.info("[Listener] status->SUCCESS OK id={}", e.contentId());
        } catch (Exception ex) {
            log.error("[Listener] storage FAIL id={}", e.contentId(), ex);
            try {
                binaryContentService.updateStatus(e.contentId(), BinaryContentStatus.FAIL);
                log.info("[Listener] status->FAIL OK id={}", e.contentId());
            } catch (Exception ex2) {
                log.error("[Listener] status->FAIL FAIL id={}", e.contentId(), ex2);
            }
        }
    }
}
