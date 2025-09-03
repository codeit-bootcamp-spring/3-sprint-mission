package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    log.info("알림 조회 요청: userId={}", userDetails.getUserDto().id());

    List<NotificationDto> notifications = notificationService
        .findAllByReceiverId(userDetails.getUserDto().id());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(notifications);
  }

  @DeleteMapping("/{notificationId}")
  @PreAuthorize("@notificationService.isNotificationOwner(#notificationId, principal.userDto.id)")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    log.info("알림 확인 처리 요청: notificationId={}, userId={}",
        notificationId, userDetails.getUserDto().id());

    notificationService.deleteByIdAndReceiverId(notificationId, userDetails.getUserDto().id());

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
