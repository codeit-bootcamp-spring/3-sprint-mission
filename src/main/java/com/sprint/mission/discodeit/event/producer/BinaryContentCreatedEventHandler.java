package com.sprint.mission.discodeit.event.producer;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventHandler {

    private final @Lazy BinaryContentStorage storage;
    private final BinaryContentRepository binaryContentRepository;
    private final ApplicationEventPublisher failurePublisher;

    @PostConstruct
    void init() {
        log.info("[BinaryEvent] listener initialized");
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(BinaryContentCreatedEvent event) {
        BinaryContentDto meta = event.meta();
        UUID id = meta.id();
        String key = event.objectKey();

        log.info("[BinaryEvent] 업로드 시작 - id={}, key={}, size={}, type={}", id, key, meta.size(),
            meta.contentType());

        try (InputStream in = event.inputStreamSupplier().get()) {
            storage.put(key, in, meta.size(), meta.contentType());

            BinaryContent binaryContent = binaryContentRepository.findById(meta.id())
                .orElseThrow(() -> new IllegalStateException("not found: " + meta.id()));
            binaryContent.updateStatus(BinaryContentStatus.SUCCESS);

            log.info("[BinaryContent] 업로드 성공 - id={}, key={}", id, key);
        } catch (Exception e) {
            BinaryContent binaryContent = binaryContentRepository.findById(meta.id())
                .orElse(null);
            if (binaryContent != null) {
                binaryContent.updateStatus(BinaryContentStatus.FAIL);
            }

            log.error("[BinaryContent] 업로드 실패 - id={}, key={}, ex={}", meta.id(), key,
                e.toString());
            String requestId = MDC.get("requestId");
            failurePublisher.publishEvent(
                S3UploadFailedEvent.from(event.meta(), e, requestId)
            );
        }
    }

}
