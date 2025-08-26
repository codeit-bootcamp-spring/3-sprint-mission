package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        RoleUpdatedEvent event = null;
        try {
            event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            String title = "권한이 변경되었습니다.";
            String content = event.oldRole() + " -> " + event.updatedRole();
            notificationService.create(event.user(), title,content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
            User user = userRepository.findById(event.userId()).orElseThrow(UserNotFoundException::new);
            String content = String.format("""
                    binaryContentId: %s,
                    fileName: %s,
                    error: 해당 파일을 S3에 저장하는데 실패하였습니다. 다시 시도해 주세요.
                    """,event.binaryContentId(),event.fileName());
            notificationService.create(user,"바이너리 컨텐츠 S3 업로드 실패",content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
