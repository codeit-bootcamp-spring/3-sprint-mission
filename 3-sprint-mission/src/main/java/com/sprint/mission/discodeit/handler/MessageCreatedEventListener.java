package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
@Component
@RequiredArgsConstructor
public class MessageCreatedEventListener {

  private final NotificationService notificationService;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    try {
      log.info("새로운 message에 대한 알림 생성. messageId={}, channelId={}",
          event.messageId(), event.channelId());

      // 1. 해당 채널의 알림 여부를 활성화한 ReadStatus 조회
      List<ReadStatus> notificationEnabledReadStatuses = readStatusRepository
          .findByChannelIdAndNotificationEnabledTrue(event.channelId());

      // 2. 메시지 작성자와 채널 정보 조회
      User author = userRepository.findById(event.authorId())
          .orElseThrow(() -> new RuntimeException("Author not found: " + event.authorId()));

      Channel channel = channelRepository.findById(event.channelId())
          .orElseThrow(() -> new RuntimeException("Channel not found: " + event.channelId()));

      // 3. 알림 생성 (메시지 작성자 제외)
      for (ReadStatus readStatus : notificationEnabledReadStatuses) {
        UUID receiverId = readStatus.getUser().getId();

        // 메시지 작성자는 알림 대상에서 제외
        if (!receiverId.equals(event.authorId())) {
          String title = String.format("%s (#%s)", author.getUsername(), channel.getName());
          String content = event.content();

          notificationService.create(receiverId, title, content);

          log.debug("Message 알림 생성됨. receiverId={}, messageId={}",
              receiverId, event.messageId());
        }
      }

      log.info("Message 알림 생성 성공. messageId={}, recipientCount={}",
          event.messageId(),
          notificationEnabledReadStatuses.size() -
              (notificationEnabledReadStatuses.stream()
                  .anyMatch(rs -> rs.getUser().getId().equals(event.authorId())) ? 1 : 0));

    } catch (Exception e) {
      log.error("Message 알림 생성 실패. messageId={}",
          event.messageId(), e);
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    try {
      log.info("권한 변경 알림 생성. userId={}, previousRole={}, newRole={}",
          event.userId(), event.previousRole(), event.newRole());

      // 권한이 변경된 당사자에게 알림 생성
      String title = "권한이 변경되었습니다.";
      String content = String.format("%s -> %s", event.previousRole(), event.newRole());

      notificationService.create(event.userId(), title, content);

      log.info("권한 변경 알림 생성 성공. userId={}", event.userId());

    } catch (Exception e) {
      log.error("권한 변경 알림 생성 실패. userId={}",
          event.userId(), e);
    }
  }

}
