package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {

        Message message = messageRepository.findById(event.messageId()).orElseThrow(() -> MessageNotFoundException.withId(event.messageId()));

        readStatusRepository.findAllByChannelIdWithUser(event.channelId())
                .stream()
                .filter(readStatus -> readStatus.isNotificationEnabled() && !readStatus.getUser().getId().equals(message.getAuthor().getId()))
                .forEach(readStatus -> {
                    String title;
                    if(readStatus.getChannel().getType().equals(ChannelType.PUBLIC)){
                        title = message.getAuthor().getUsername() + " (#" + readStatus.getChannel().getName() + ")";
                    }
                    else{
                        title = message.getAuthor().getUsername() + " (#" + message.getAuthor().getUsername() + ")";
                    }
                    NotificationDto notification = notificationService.create(readStatus.getUser(), title, message.getContent());
                    log.info("알림 이벤트 생성완료 id:{}",notification.id());
                });

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        String title = "권한이 변경되었습니다.";
        String content = event.oldRole() + " -> " + event.updatedRole();
        notificationService.create(event.user(), title,content);
    }

}
