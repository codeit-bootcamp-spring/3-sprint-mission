package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        log.info("[NotificationController] 알림 조회 요청");

        List<NotificationDto> result = notificationService.getNotifications();

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> checkNotifications(@PathVariable UUID notificationId) {
        log.info("[NotificationController] 알림 읽음 처리- notificationId: {}", notificationId);

        notificationService.checkNotifications(notificationId);

        log.info("[NotificationController] 알림 읽음 처리 완료- notificationId: {}", notificationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
