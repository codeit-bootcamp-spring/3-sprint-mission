package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;


    // 채널의 알림 여부를 활성화한 유저에게 알림 생성
    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        UUID channelId = event.messageDto().channelId();
        UUID authorId = event.messageDto().author().id();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
        Message message = messageRepository.findById(event.messageDto().id())
            .orElseThrow(() -> new MessageNotFoundException(event.messageDto().id()));

        String title = message.getAuthor().getUsername() + " (#" + channel.getName() + ")";

        String content = message.getContent() == null ? " " : message.getContent();

        // 해당 채널의 알림 여부를 활성화한 ReadStatus를 조회
        List<ReadStatus> foundReadStatus = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId);

        // 작성자 제외하고 발송할 목록
        List<UUID> receiverIds = foundReadStatus.stream()
            .map(readStatus -> readStatus.getUser().getId())
            .filter(uuid -> !uuid.equals(authorId))
            .toList();

        List<User> receivers = userRepository.findAllById(receiverIds);

        // 알림 엔티티 생성하기
        List<Notification> notifications = receivers.stream()
            .map(user -> new Notification(user, title, content))
            .toList();

        notificationService.saveAllNotifications(notifications);
    }

    // 권한이 변경된 당사자에게 알림 생성
    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        UUID userId = event.userId();
        Role oldRole = event.oldRole();
        Role newRole = event.newRole();

        if (oldRole.equals(newRole)) return;

        String title = "권한이 변경되었습니다.";
        String content = oldRole + " -> " + newRole;

        User receiver =  userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Notification notification = new Notification(receiver, title, content);

        notificationRepository.save(notification);
    }

    // S3에 파일 업로드 실패 시 알림 생성
    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(S3UploadFailEvent event) {

        String jobName = "s3 파일 업로드 작업 실패";
        UUID binaryContentId = event.binaryContentId();
        String requestId = event.requestId();
        String errorMsg = event.errorMsg();

        notificationService.notifyS3StoreFail(jobName, binaryContentId, requestId, errorMsg);
    }

}
