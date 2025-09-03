package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> list(
        @AuthenticationPrincipal DiscodeitUserDetails principal
    ) {
        UUID me = principal.getUserDto().id();

        List<NotificationDto> notification = notificationService.findMyNotifications(me).stream()
            .toList();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(notification);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID notificationId,
        @AuthenticationPrincipal DiscodeitUserDetails principal
    ) {
        UUID me = principal.getUserDto().id();
        notificationService.deleteMyNotification(me, notificationId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
