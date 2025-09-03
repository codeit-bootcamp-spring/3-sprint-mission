package com.sprint.mission.discodeit.event.listener;


import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

    private final BinaryContentStorage storage;
    private final BinaryContentService binaryContentService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Async("eventExecutor")
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {
        log.info("바이너리 컨텐츠 스토리지 저장 로직 시작 id: {}", event.binaryContentId());
        try {
            storage.put(event.binaryContentId(), event.bytes());
        }catch (Exception e) {
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
            return;
        }
        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
        log.info("바이너리 컨텐츠 저장완료!");
    }

    @Recover
    public void recover(Exception e, BinaryContentCreatedEvent event) {

        String requestId = (String) MDC.get("requestId");
        String title = "S3 파일 업로드 실패";

        String content = String.format("""
                Request ID: %s
                BinaryContent ID: %s
                Error: %s
                """, requestId, event.binaryContentId(), e.getMessage());

        User admin = userRepository.findByUsername("admin").get();
        notificationService.create(admin,title,content);
    }
}
