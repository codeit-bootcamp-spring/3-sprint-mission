package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.NotificationException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(MessageCreatedEvent event) {
        Message message = event.message();

        Channel channel = message.getChannel();
        String channelName = channel.getName();
        UUID channelId = channel.getId();

        User author = message.getAuthor();
        UUID authorId = author.getId();

        try {
            List<User> users = readStatusRepository.findAllByChannelIdWithUserAndNotificationEnabledTrue(channelId)
                    .stream()
                    .map(ReadStatus::getUser)
                    .toList();

            for (User user : users) {
                // 메시지 작성자는 알림 제외
                if (user.getId().equals(authorId)) {
                    continue;
                }

                String title = author.getUsername() + "(#" + channelName + ")";
                Notification notification = new Notification(
                        user,
                        title,
                        message.getContent());

                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            throw new NotificationException("알림 생성에 실패했습니다.");
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RoleUpdatedEvent event) {
        User user = event.user();
        Role oldRole = event.oldRole();
        Role newRole = event.user().getRole();

        try {
            String title = "권한이 변경되었습니다.";
            String content = oldRole.name() + " -> " + newRole.name();

            Notification notification = new Notification(
                    user,
                    title,
                    content
            );

            notificationRepository.save(notification);
        } catch (Exception e) {
            throw new NotificationException("알림 생성에 실패했습니다.");
        }
    }
}
