package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

    private final BinaryContentStorage storage;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {

        log.debug("[BinaryContentCreatedEventListener] onBinaryContent id: {}, fileName: {}",
            event.binaryContent().getId(),
            event.binaryContent().getFileName());

        UUID id = event.binaryContent().getId();
        byte[] data = event.data();

        // 바이너리 데이터 저장
        storage.put(id, data);
    }
}
