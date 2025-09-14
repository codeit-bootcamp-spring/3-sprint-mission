package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BroadcastListener {

    private final ObjectMapper objectMapper;
    private final SseService sseService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String EVENT_NAME_NOTIFICATION_CREATED = "notifications.created";
    private static final String SSE_EVENT_USER_UPDATED = "users.updated";

    @KafkaListener(
        topics = "discodeit.MessageCreatedEvent",
        containerFactory = "broadcastKafkaListenerContainerFactory"
    )
    public void broadcastMessage(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                MessageCreatedEvent.class);
            UUID channelId = event.data().channelId();
            String destination = "/sub/channels." + channelId + ".messages";

            log.debug("[BroadcastListener] WS 전송 -> {} payload = {}", destination, event.data());
            messagingTemplate.convertAndSend(destination, event.data());
        } catch (JsonProcessingException e) {
            log.warn("[BroadcastListener] 변환 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
        topics = "discodeit.NotificationCreatedEvent",
        containerFactory = "broadcastKafkaListenerContainerFactory"
    )
    public void broadcastNotifications(String kafkaEvent) {
        try {
            NotificationCreatedEvent event = objectMapper.readValue(kafkaEvent,
                NotificationCreatedEvent.class);
            sseService.send(List.of(event.receiverId()), EVENT_NAME_NOTIFICATION_CREATED,
                event.dto());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
        topics = "discodeit.UserLogInOutEvent",
        containerFactory = "broadcastKafkaListenerContainerFactory"
    )
    public void broadcastUserStatus(String kafkaEvent) throws JsonProcessingException {
        UserLogInOutEvent event = objectMapper.readValue(kafkaEvent, UserLogInOutEvent.class);
        User user = userRepository.findById(event.userId())
            .orElseThrow(() -> new NotFoundUserException(event.userId()));
        UserResponseDto userDto = userMapper.toDto(user);

        sseService.broadcast(SSE_EVENT_USER_UPDATED, userDto);

        log.debug("[Broadcast] SSE users.updated -> userId={} online={}", user.getId(),
            userDto.online());
    }
}
