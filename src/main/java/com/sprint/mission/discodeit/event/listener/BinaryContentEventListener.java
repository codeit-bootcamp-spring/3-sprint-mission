package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {
        log.info("파일 데이터 저장 이벤트 처리 시작 - binaryContentId: {}", event.binaryContentId());

        try {
            binaryContentStorage.put(event.binaryContentId(), event.bytes());
            log.debug("파일 데이터 저장 완료");
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);

        } catch (Exception e) {
            log.error("파일 데이터 저장 실패 - binaryContentId: {}", event.binaryContentId(), e);
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
        }
    }
}