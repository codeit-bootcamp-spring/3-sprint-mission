package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(
        @RequestParam(required = false) UUID receiverId,
        Authentication authentication) {

        UUID targetReceiverId;
        if (receiverId != null) {
            targetReceiverId = receiverId;
        } else {
            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
            targetReceiverId = userDetails.getUserId();
        }

        log.info("[API-REQUEST] 알림 목록 조회 요청 - 대상 사용자: {}", targetReceiverId);

        long startTime = System.currentTimeMillis();
        List<NotificationDto> notifications = notificationService.findAllByReceiverId(targetReceiverId);
        long endTime = System.currentTimeMillis();

        log.info("[PERFORMANCE] 알림 목록 조회 완료 - 응답시간: {}ms, 알림 수: {}개",
            (endTime - startTime), notifications.size());

        return ResponseEntity.ok().body(notifications);
    }

    @DeleteMapping(path = "{notificationId}")
    public ResponseEntity<Void> deleteNotification(
        @PathVariable UUID notificationId,
        Authentication authentication) {

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUserId();

        log.info("[API-REQUEST] 알림 삭제 요청 - 알림 ID: {}, 요청자: {}", notificationId, currentUserId);

        long startTime = System.currentTimeMillis();
        notificationService.delete(notificationId, currentUserId);
        long endTime = System.currentTimeMillis();

        log.info("[PERFORMANCE] 알림 삭제 완료 - 응답시간: {}ms", (endTime - startTime));

        return ResponseEntity.noContent().build();
    }
}
