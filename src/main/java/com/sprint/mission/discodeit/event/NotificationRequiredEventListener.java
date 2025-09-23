package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.payload.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.payload.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Async("asyncExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        log.debug("MessageCreatedEvent 수신: {}", event);

        // 알림 설정이 켜진 유저만
        List<ReadStatus> readStatuses = readStatusRepository
                .findAllByChannelIdAndNotificationEnabledTrue(event.channelId());

        for (ReadStatus rs : readStatuses) {
            // 보낸 사람 제외
            if (!rs.getUser().getId().equals(event.authorId())) {
                String title = event.authorName() + " (#" + event.channelName() + ")";
                String content = event.messageContent();
                notificationService.send(rs.getUser(), title, content);
            }
        }
    }

    @Async("asyncExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        log.debug("RoleUpdatedEvent 수신: {}", event);

        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자"));

        String title = "권한이 변경되었습니다.";
        String content = event.oldRole() + " → " + event.newRole();

        notificationService.send(user, title, content);
    }
}