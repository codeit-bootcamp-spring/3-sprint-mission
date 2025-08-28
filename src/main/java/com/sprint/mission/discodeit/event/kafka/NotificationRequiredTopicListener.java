package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ChannelRepository channelRepository;

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      MessageResponse msg = event.message();
      UUID channelId = msg.channelId();
      Channel channel = channelRepository.findById(channelId)
          .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));
      readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId)
          .stream()
          .filter(rs -> !rs.getUser().getId().equals(msg.author().id()))
          .forEach(rs -> {
            String title = msg.author().username()
                + (channel.getType() == ChannelType.PUBLIC ?
                String.format(" (#%s)", channel.getName()) : "");
            String content = msg.content();
            Notification notification = notificationService.create(rs.getUser(), title, content);
            log.debug("[Notification created] receiver: {}, title: {}, content: {}",
                notification.getReceiver(), notification.getTitle(), notification.getTitle());
          });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      String title = "권한이 변경되었습니다.";
      String content = event.oldRole().name() + " -> " + event.newRole().name();
      userRepository.findById(event.userId())
          .ifPresent(user -> notificationService.create(user, title, content));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      BinaryContentUploadFailedEvent event = objectMapper.readValue(kafkaEvent,
          BinaryContentUploadFailedEvent.class);
      userRepository.findAllByRole(Role.ADMIN)
          .forEach(admin -> {
            String body = "RequestId: " + event.requestId()
                + "\nBinaryContentId: " + event.binaryContentId()
                + "\nError: " + event.errorMessage();
            notificationService.create(admin, "BinaryContent 저장 실패", body);
            log.warn("[Notification created] BinaryContent 저장 실패 알림 전송: {} (receiver: {})",
                body, admin.getEmail());
          });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
