package com.sprint.mission.discodeit.event;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.listener.BinaryContentCreatedEventListener;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentEventListenerTest {

  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentService binaryContentService;

  @InjectMocks
  private BinaryContentCreatedEventListener listener;

  @Test
  void 이벤트를_받아_파일을_저장하고_성공_상태를_설정한다() {
    UUID id = UUID.randomUUID();
    byte[] bytes = new byte[]{1, 2, 3};
    BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(id, bytes);

    listener.on(event);

    then(binaryContentStorage).should().put(id, bytes);
    then(binaryContentService).should().updateStatus(id, BinaryContentStatus.SUCCESS);
  }

  @Test
  void 파일_저장에_실패하면_실패_상태를_설정한다() {
    UUID id = UUID.randomUUID();
    byte[] bytes = new byte[]{1, 2, 3};
    BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(id, bytes);

    willThrow(new RuntimeException()).given(binaryContentStorage).put(id, bytes);

    listener.on(event);

    then(binaryContentService).should().updateStatus(id, BinaryContentStatus.FAIL);
  }
}
