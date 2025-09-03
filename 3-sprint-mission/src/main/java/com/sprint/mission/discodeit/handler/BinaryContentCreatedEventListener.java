package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage storage;
  private final BinaryContentRepository binaryContentRepository;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    runWithStatusUpdate(
        event.binaryContentId(),
        () -> {
          storage.put(event.binaryContentId(), event.data());
          log.info("Binary content 저장. id={}, size={}, contentType={}, filename={}",
              event.binaryContentId(), event.size(), event.contentType(), event.originalFilename());
        }
    );
  }

  private void runWithStatusUpdate(UUID contentId, Runnable action) {
    try {
      action.run();
      markStatus(contentId, BinaryContentStatus.SUCCESS);
    } catch (Exception e) {
      log.error("Binary content 저장 실패. id={}", contentId, e);
      safelyMarkFail(contentId);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void markStatus(UUID contentId, BinaryContentStatus status) {
    binaryContentRepository.findById(contentId).ifPresent(entity -> {
      entity.updateStatus(status);
      binaryContentRepository.save(entity);
    });
  }

  private void safelyMarkFail(UUID contentId) {
    try {
      markStatus(contentId, BinaryContentStatus.FAIL);
    } catch (Exception ex) {
      log.error("FAIL 상태로 설정 실패. id={}", contentId, ex);
    }
  }
}
