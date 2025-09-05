package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.NotificationDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.web.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Kafka 토픽에서 이벤트를 구독하여 알림을 생성하는 리스너입니다.
 * 
 * <p>기존의 NotificationRequiredEventListener를 대체하여 Kafka 기반의 
 * 비동기 알림 생성을 담당합니다.</p>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

    private static final String LISTENER_NAME = "[NotificationRequiredTopicListener] ";
    
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final CacheManager cacheManager;
    private final ChannelRepository channelRepository;
    private final SseService sseService;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;

    /**
     * MessageCreatedEvent를 Kafka에서 구독하여 알림을 생성합니다.
     * 
     * @param kafkaEvent Kafka에서 수신한 JSON 형태의 이벤트
     */
    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    @Transactional
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            log.info(LISTENER_NAME + "MessageCreatedEvent 수신 - payload: {}", kafkaEvent);
            
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
            
            // 기존 NotificationRequiredEventListener의 로직을 여기로 이동
            List<User> users = readStatusRepository.findAllByChannelIdWithUserAndNotificationEnabledTrue(event.message().channelId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .toList();

            for (User user : users) {
                if (user.getId().equals(event.message().author().id())) continue; // 메시지 작성자는 제외

                try {
                    String authorName = event.message().author().username();
                    UUID channelId = event.message().channelId();
                    Channel channel = channelRepository.findById(channelId)
                            .orElseThrow(() -> new ChannelNotFoundException("channelId: " + channelId));
                    String channelName = channel.getName();
                    UUID userId = user.getId();

                    String title = String.format("%s(#%s)", authorName, channelName);

                    NotificationDto notificationDto = notificationService.create(user, title, event.message().content());
                    sseService.send(List.of(userId), "MessageCreatedEvent", notificationDto);

                    Cache cache = cacheManager.getCache("notificationByUser");
                    if (cache != null) cache.evict(user.getId());

                } catch (Exception userException) {
                    log.error(LISTENER_NAME + "개별 사용자 알림 생성 실패 - userId={}, messageId={}", 
                            user.getId(), event.message().id(), userException);
                }
            }
            
            log.info(LISTENER_NAME + "MessageCreatedEvent 처리 완료 - messageId: {}",
                    event.message().id());
            
        } catch (JsonProcessingException e) {
            log.error(LISTENER_NAME + "MessageCreatedEvent JSON 파싱 실패", e);
            throw new RuntimeException("이벤트 파싱 실패", e);
        } catch (Exception e) {
            log.error(LISTENER_NAME + "MessageCreatedEvent 처리 실패", e);
        }
    }

    /**
     * RoleUpdatedEvent를 Kafka에서 구독하여 알림을 생성합니다.
     * 
     * @param kafkaEvent Kafka에서 수신한 JSON 형태의 이벤트
     */
    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    @Transactional
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            log.info(LISTENER_NAME + "RoleUpdatedEvent 수신 - payload: {}", kafkaEvent);
            
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            
            // 권한 변경 알림 생성 로직
            String title = "권한이 변경되었습니다.";
            String content = String.format("변경 전: %s\n변경 후: %s", 
                    event.oldRole().name(), event.newRole().name());

            // 권한이 변경된 사용자에게 알림 생성
            try {
                User targetUser = userRepository.findById(event.userId())
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + event.userId()));

                NotificationDto notificationDto = notificationService.create(targetUser, title, content);
                sseService.send(List.of(event.userId()), "RoleUpdatedEvent", notificationDto);

                Cache cache = cacheManager.getCache("notificationByUser");
                if (cache != null) cache.evict(targetUser.getId());

                log.info(LISTENER_NAME + "RoleUpdatedEvent 알림 생성 완료 - userId: {}, oldRole: {}, newRole: {}",
                        event.userId(), event.oldRole(), event.newRole());
                
            } catch (Exception userException) {
                log.error(LISTENER_NAME + "RoleUpdatedEvent 알림 생성 실패 - userId: {}, oldRole: {}, newRole: {}", 
                        event.userId(), event.oldRole(), event.newRole(), userException);
            }
            
        } catch (JsonProcessingException e) {
            log.error(LISTENER_NAME + "RoleUpdatedEvent JSON 파싱 실패", e);
            throw new RuntimeException("이벤트 파싱 실패", e);
        } catch (Exception e) {
            log.error(LISTENER_NAME + "RoleUpdatedEvent 처리 실패", e);
        }
    }

    /**
     * S3UploadFailedEvent를 Kafka에서 구독하여 관리자에게 알림을 생성합니다.
     * 
     * @param kafkaEvent Kafka에서 수신한 JSON 형태의 이벤트
     */
    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    @Transactional
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            log.info(LISTENER_NAME + "S3UploadFailedEvent 수신 - payload: {}", kafkaEvent);
            
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
            
            // S3 업로드 실패 알림 생성 로직
            List<User> adminUsers = userRepository.findAllByRoleAdmin();

            if (adminUsers.isEmpty()) {
                log.warn(LISTENER_NAME + "ADMIN 권한을 가진 사용자가 없습니다 - requestId: {}", event.requestId());
                return;
            }

            String title = "파일 업로드 실패";
            String content = String.format("RequestId: %s\nBinaryContentId: %s\nError: %s\n발생 시간: %s", 
                    event.requestId(), 
                    event.binaryContentId(), 
                    event.reason(),
                    event.occurredAt());

            for (User user : adminUsers) {
                try {
                    NotificationDto notificationDto = notificationService.create(user, title, content);
                    log.info(LISTENER_NAME + "관리자에게 알림 생성 성공 - adminId: {}, requestId: {}, notification: {}",
                            user.getId(), event.requestId(), notificationDto);
                } catch (Exception adminException) {
                    log.error(LISTENER_NAME + "관리자에게 알림 생성 실패 - adminId: {}, requestId: {}",
                            user.getId(), event.requestId(), adminException);
                }
            }
            
            log.info(LISTENER_NAME + "S3UploadFailedEvent 처리 완료 - requestId: {}, adminCount: {}",
                    event.requestId(), adminUsers.size());
            
        } catch (JsonProcessingException e) {
            log.error(LISTENER_NAME + "S3UploadFailedEvent JSON 파싱 실패", e);
            throw new RuntimeException("이벤트 파싱 실패", e);
        } catch (Exception e) {
            log.error(LISTENER_NAME + "S3UploadFailedEvent 처리 실패", e);
        }
    }
}
