package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * PackageName  : com.sprint.mission.discodeit.event
 * FileName     : BinaryContentCreatedEvent
 * Author       : dounguk
 * Date         : 2025. 8. 27.
 */
@Component
@RequiredArgsConstructor
public class BinaryContentEventHandler {
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @TransactionalEventListener
    public void on(BinaryContentCreatedEvent event) {
        BinaryContent binaryContent = event.getData();
        try {
            binaryContentStorage.put(binaryContent.getId(), event.getBytes());
            binaryContentService.updatedStatus(binaryContent.getId(), BinaryContentStatus.SUCCESS);
        } catch (RuntimeException e) {
            binaryContentService.updatedStatus(binaryContent.getId(), BinaryContentStatus.FAIL);
        }
    }


}
