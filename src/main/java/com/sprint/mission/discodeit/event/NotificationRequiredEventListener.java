package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {

        Set<UUID> receiverIds = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
                        event.channelId())
                .stream().map(rs -> rs.getUser().getId())
                .filter(id -> !id.equals(event.authorId()))
                .collect(Collectors.toSet());

        String title = (event.channelType() == ChannelType.PUBLIC)
                ? event.authorUsername() + " (#" + event.channelName() + ")"
                : event.authorUsername();

        notificationService.create(receiverIds, title, event.content());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        String title = "권한이 변경되었습니다.";
        String content = event.role().name() + " -> " + event.newRole().name();
        notificationService.create(Set.of(event.userid()), title, content);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        String requestId = event.getRequestId();
        UUID binaryContentId = event.getBinaryContentId();
        Throwable e = event.getE();

        String title = "S3 파일 업로드 실패";

        String content = """
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """.formatted(requestId, binaryContentId, e.getMessage());

        Set<UUID> adminIds = new HashSet<>(userRepository.findAllIdsByRole(Role.ADMIN));

        notificationService.create(adminIds, title, content);
    }
}