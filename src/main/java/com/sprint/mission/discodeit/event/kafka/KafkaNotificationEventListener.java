package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.MessageCreateEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.messaging.type", havingValue = "kafka")
public class KafkaNotificationEventListener {

    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;
    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @Value("${server.instance-id:default}")
    private String instanceId;

    @KafkaListener(
        topics = "discodeit.MessageCreateEvent",
        groupId = "notification-group-${server.instance-id:default}"
    )
    public void handleMessageCreateEvent(String kafkaEvent) {
        try {
            MessageCreateEvent event = objectMapper.readValue(kafkaEvent, MessageCreateEvent.class);

            log.info("[Kafka Consumer] 메시지 생성 알림 이벤트 수신 - 인스턴스: {}, 메시지 ID: {}",
                instanceId, event.messageId());

            var notificationEnabledStatuses = readStatusRepository
                .findAllByChannelIdAndNotificationEnabledTrue(event.channelId());

            for (var readStatus : notificationEnabledStatuses) {
                if (!readStatus.getUser().getId().equals(event.authorId())) {
                    NotificationDto notification = notificationService.create(
                        readStatus.getUser().getId(),
                        event.channelName() + " 채널에 새 메시지가 도착했습니다.",
                        event.content()
                    );

                    //  Redis 기반 SSE로 전송 - 모든 인스턴스에 전파됨
                    sseService.send(
                        List.of(readStatus.getUser().getId()),
                        "notifications.new",
                        notification
                    );

                    log.info("[Kafka Consumer] 새 메시지 알림 완료 - 수신자: {}, 인스턴스: {}, 알림ID: {}",
                        readStatus.getUser().getId(), instanceId, notification.id());
                }
            }
        } catch (Exception e) {
            log.error("[Kafka Consumer] 메시지 생성 이벤트 처리 실패 - 인스턴스: {}", instanceId, e);
        }
    }

    @KafkaListener(
        topics = "discodeit.RoleUpdatedEvent",
        groupId = "role-notification-group-${server.instance-id:default}"
    )
    public void handleRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            log.info("[Kafka Consumer] 권한 변경 알림 이벤트 수신 - 인스턴스: {}, 사용자 ID: {}",
                instanceId, event.userId());

            String title = "권한이 변경되었습니다.";
            String content = String.format("%s -> %s", event.oldRole(), event.newRole());

            NotificationDto notification = notificationService.create(event.userId(), title, content);

            sseService.send(
                List.of(event.userId()),
                "notifications.role_changed",
                notification
            );

            log.info("[Kafka Consumer] 권한 변경 알림 완료 - 수신자: {}, 인스턴스: {}, 알림ID: {}",
                event.userId(), instanceId, notification.id());
        } catch (Exception e) {
            log.error("[Kafka Consumer] 권한 변경 이벤트 처리 실패 - 인스턴스: {}", instanceId, e);
        }
    }
}
