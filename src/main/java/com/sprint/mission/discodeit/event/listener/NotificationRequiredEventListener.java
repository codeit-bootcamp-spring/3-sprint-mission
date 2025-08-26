package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {

        readStatusRepository.findAllByChannelIdWithUser(event.channelId())
                .stream()
                .filter(readStatus -> readStatus.isNotificationEnabled() && !readStatus.getUser().getId().equals(event.authorId()))
                .forEach(readStatus -> {
                    String title;
                    if(event.channelType().equals(ChannelType.PUBLIC)){
                        title = event.authorUsername() + " (#" + event.channelName() + ")";
                    }
                    else{
                        title = event.authorUsername() + " (#" + event.authorUsername() + ")";
                    }
                    notificationService.create(readStatus.getUser(), title, event.content());
                    log.info("알림 이벤트 생성완료 id");
                });

    }

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        String title = "권한이 변경되었습니다.";
        String content = event.oldRole() + " -> " + event.updatedRole();
        notificationService.create(event.user(), title,content);
    }

}
