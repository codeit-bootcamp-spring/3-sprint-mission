package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Value("${discodeit.admin.username}")
  private String adminUsername;

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    String requestId = event.getRequestId();
    UUID binaryContentId = event.getBinaryContentId();
    Throwable e = event.getE();

    String title = "S3 파일 업로드 실패";

    StringBuffer sb = new StringBuffer();
    sb.append("RequestId: ").append(requestId).append("\n");
    sb.append("BinaryContentId: ").append(binaryContentId).append("\n");
    sb.append("Error: ").append(e.getMessage()).append("\n");
    String content = sb.toString();

    Set<UUID> receiverIds = userRepository.findByUsername(adminUsername)
        .map(user -> Set.of(user.getId()))
        .orElse(Set.of());

    notificationService.create(receiverIds, title, content);
  }

}
