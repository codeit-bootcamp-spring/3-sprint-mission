package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;
    private final ApplicationEventPublisher eventPublisher;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
        log.info("비동기 바이너리 데이터 저장 시작 - 스레드 : {}, ID : {}",
            Thread.currentThread().getName(), event.binaryContentId());

        try {
            binaryContentStorage.put(event.binaryContentId(), event.bytes());
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);

            log.info("비동기 바이너리 데이터 저장 완료 - 스레드 : {}, ID : {}",
                Thread.currentThread().getName(), event.binaryContentId());
        } catch (Exception e) {
            log.error("비동기 바이너리 데이터 저장 실패 - 스레드 : {}, ID : {}, error : {}",
                Thread.currentThread().getName(), event.binaryContentId(), e.getMessage());
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);

            eventPublisher.publishEvent(new S3UploadFailedEvent(
                event.binaryContentId(),
                event.fileName(),
                e.getMessage(),
                "S3 업로드 중 오류 발생"
            ));
        }
    }
}