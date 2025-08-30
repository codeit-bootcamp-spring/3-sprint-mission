package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private static final int CONTENT_SNIPPET_MAX = 100;
    private static final int SAMPLE_LOG_LIMIT = 5;

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        long t0 = System.nanoTime();
        int success = 0, failed = 0;

        log.info(
            "[Notification] event received: messageId={}, channelId={}, authorId={}, contentLen={}, attachments={}",
            event.id(), event.channelId(), event.author().id(),
            event.content() == null ? 0 : event.content().length(),
            event.attachments() == null ? 0 : event.attachments().size());

        // 채널명
        String channelName = channelRepository.findById(event.channelId())
            .map(Channel::getName).orElse("알 수 없는 채널");

        // 보낸 사람 표기
        String senderDisplay = userRepository.findById(event.author().id())
            .map(User::getUsername).filter(s -> !s.isBlank()).orElse("보낸 사람");

        // 수신자 조회
        List<UUID> receiverIds = readStatusRepository
            .findAllByChannelIdAndNotificationEnabledTrueWithUser(event.channelId())
            .stream()
            .filter(ReadStatus::isNotificationEnabled) // (중복 방어지만 유지)
            .map(ReadStatus::getUser).map(User::getId)
            .filter(uid -> !uid.equals(event.author().id()))
            .distinct()
            .collect(Collectors.toList());

        log.info("[Notification] receivers resolved: count={}, sample={}",
            receiverIds.size(),
            receiverIds.stream().limit(SAMPLE_LOG_LIMIT).toList());

        if (receiverIds.isEmpty()) {
            log.debug("[Notification] no receivers → skip: channelId={}, messageId={}",
                event.channelId(), event.id());
            return;
        }

        String title = String.format("%s (#%s)", senderDisplay, channelName);
        String content = snippet(event.content(), CONTENT_SNIPPET_MAX);

        log.debug("[Notification] title='{}', contentSnippet='{}'", title, content);

        for (UUID rid : receiverIds) {
            try {
                log.debug("[Notification] create start: rid={}, msgId={}", rid, event.id());
                notificationService.create(rid, title, content); // 저장 로직 내부에도 로그 권장
                success++;
            } catch (Exception e) {
                failed++;
                log.error("[Notification] create failed: rid={}, msgId={}, err={}",
                    rid, event.id(), e.toString(), e);
            }
        }

        long ms = (System.nanoTime() - t0) / 1_000_000;
        log.info(
            "[Notification] done: messageId={}, attempted={}, success={}, failed={}, took={}ms",
            event.id(), receiverIds.size(), success, failed, ms);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        log.info("[Notification] role update event received: userId={}, {} -> {}",
            event.userId(), event.oldRole(), event.newRole());

        String title = "권한이 변경되었습니다.";
        String content = String.format("%s -> %s", event.oldRole(), event.newRole());

        try {
            notificationService.create(event.userId(), title, content);
            log.info("[Notification] role update notification created: userId={}", event.userId());
        } catch (Exception e) {
            log.error("[Notification] role update notification failed: userId={}, err={}",
                event.userId(), e.toString(), e);
        }
    }

    private String snippet(String raw, int max) {
        if (raw == null) {
            return "";
        }
        String t = raw.strip();
        return t.length() <= max ? t : t.substring(0, max) + "…";
    }
}
