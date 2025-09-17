package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreateEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class NotificationRequiredTopicListener {
//
//    private final NotificationService notificationService;
//    private final ReadStatusRepository readStatusRepository;
//    private final ObjectMapper objectMapper;
//    private final ApplicationEventPublisher eventPublisher;
//
//    @KafkaListener(topics = "discodeit.MessageCreateEvent")
//    public void onMessageCreateEvent(String kafkaEvent) {
//
//        log.info("[Kafka Consumer] Raw 메시지 수신: {}", kafkaEvent);
//
//        try {
//            MessageCreateEvent event = objectMapper.readValue(kafkaEvent, MessageCreateEvent.class);
//
//            log.info("[Kafka Consumer] MessageCreateEvent 파싱 성공 - 스레드: {}, 채널: {}",
//                Thread.currentThread().getName(), event.channelId());
//
//
//            List<ReadStatus> readStatuses = readStatusRepository
//                .findAllByChannelIdAndNotificationEnabledTrue(event.channelId());
//
//            log.info("[Kafka Consumer] ReadStatus 조회 완료 - 채널: {}, 총 사용자: {}명",
//                event.channelId(), readStatuses.size());
//
//
//            readStatuses.stream()
//                .filter(readStatus -> !readStatus.getUser().getId().equals(event.authorId()))
//                .forEach(readStatus -> {
//                    String title = String.format("%s (#%s)",event.authorUsername(), event.channelName());
//
//                    log.debug("Kafka 메시지 알림 생성 - 수신자 : {}, 제목 : {}", readStatus.getUser().getUsername(), title);
//                    notificationService.create(readStatus.getUser().getId(), title, event.content());
//                });
//
//            log.info("[Kafka Consumer] Kafka 메시지 알림 처리 완료 - 스레드 : {}, 채널 : {}, 알림 대상 : {}", Thread.currentThread().getName(), event.channelId(), (int) readStatuses.stream()
//                .filter(readStatus -> !readStatus.getUser().getId().equals(event.authorId())).count());
//
//        } catch (JsonProcessingException e) {
//            log.error("[Kafka Consumer] Kafka 메시지 생성 이벤트 파싱 실패 : {}", e.getMessage());
//            throw new RuntimeException(e);
//        } catch (Exception e) {
//            log.error("[Kafka Consumer] 예상치 못한 오류: {}", e.getMessage(), e);
//        }
//    }
//
//    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
//    public void onRoleUpdatedEvent(String kafkaEvent) {
//        try {
//            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
//
//            log.info("Kafka에서 권한 변경 이벤트 수신 - 스레드 : {}, 사용자 : {}", Thread.currentThread().getName(), event.userId());
//
//            String title = "권한이 변경되었습니다.";
//            String content = String.format("%s -> %s", event.oldRole(), event.newRole());
//
//            notificationService.create(event.userId(), title, content);
//
//            log.info("Kafka 권한 변경 알림 처리 완료 - 스레드 : {}, 사용자 : {}", Thread.currentThread().getName(), event.userId());
//        } catch (JsonProcessingException e) {
//            log.error("Kafka 권한 변경 이벤트 파싱 실패 : {}", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
//    public void onS3UploadFailedEvent(String kafkaEvent) {
//        try {
//            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
//
//            log.info("Kafka에서 S3 업로드 실패 이벤트 수신 - 스레드 : {}", Thread.currentThread().getName());
//
//            log.info("Kafka S3 업로드 실패 알림 처리 완료 - 스레드 : {}", Thread.currentThread().getName());
//        } catch (JsonProcessingException e) {
//            log.error("Kafka S3 업로드 실패 이벤트 파싱 실패 : {}",e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @KafkaListener(topics = "discodeit.UserLogInOutEvent")
//    public void onUserLogInOutEvent(String kafkaEvent) {
//        try {
//            UserLogInOutEvent event = objectMapper.readValue(kafkaEvent, UserLogInOutEvent.class);
//
//            log.info("[Kafka Consumer] 사용자 로그인/로그아웃 이벤트 수신 - 스레드: {}, 사용자: {}, 로그인: {}",
//                Thread.currentThread().getName(), event.userId(), event.isLoggedIn());
//
//            // 로컬 이벤트로 재발행하여 WebSocket 등에서 처리할 수 있도록 함
//            eventPublisher.publishEvent(event);
//
//            log.info("[Kafka Consumer] 사용자 로그인/로그아웃 이벤트 처리 완료 - 사용자: {}", event.userId());
//        } catch (JsonProcessingException e) {
//            log.error("[Kafka Consumer] 사용자 로그인/로그아웃 이벤트 파싱 실패: {}", e.getMessage());
//            throw new RuntimeException(e);
//        } catch (Exception e) {
//            log.error("[Kafka Consumer] 예상치 못한 오류: {}", e.getMessage(), e);
//        }
//    }
//}