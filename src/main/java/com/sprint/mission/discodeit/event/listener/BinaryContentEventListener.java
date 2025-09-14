package com.sprint.mission.discodeit.event.listener;

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

    // 이벤트를 발행한 메인 서비스의 트랜잭션이 커밋되었을 때 리스너가 실행
    @Async("fileTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {

        UUID fileId = event.binaryContentId();
        byte[] bytes = event.bytes();

        log.info("[BinaryContentEventListener] BinaryContentStorage에 저장 중");
        try {
            binaryContentStorage.put(fileId, bytes);
            binaryContentService.updateStatus(fileId, BinaryContentStatus.SUCCESS);
            log.debug("[BinaryContentEventListener] 저장 성공 - 바이너리 파일 id={}", fileId);


        } catch (Exception e) {
            binaryContentService.updateStatus(fileId, BinaryContentStatus.FAIL);
            log.error("[BinaryContentEventListener] 저장 실패 - 바이너리 파일 id={}", fileId, e);
        }
    }
}
