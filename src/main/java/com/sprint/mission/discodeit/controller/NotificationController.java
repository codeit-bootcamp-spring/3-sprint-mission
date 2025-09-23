package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> findMyNotifications(
            @AuthenticationPrincipal DiscodeitUserDetails user) {
        UUID userId = user.getId();
        log.info("알림 조회 요청: userId={}", userId);

        List<NotificationDto> notifications = notificationService.findAllByReceiverId(userId);
        log.info("알림 개수: {}", notifications.size());

        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> confirm(
            @AuthenticationPrincipal DiscodeitUserDetails user,
            @PathVariable UUID notificationId
    ) {
        notificationService.confirm(notificationId, user.getId());
        return ResponseEntity.noContent().build();
    }
}