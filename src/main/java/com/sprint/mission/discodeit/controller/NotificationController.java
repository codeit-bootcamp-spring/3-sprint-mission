package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.response.NotificationDto;
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

@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(
        @AuthenticationPrincipal DiscodeitUserDetails principal
    ) {
        List<NotificationDto> result = notificationService.findAllNotifications(principal.getId());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
        @PathVariable("notificationId")UUID notificationId,
        @AuthenticationPrincipal DiscodeitUserDetails principal
    ) {
        notificationService.deleteNotification(notificationId, principal.getId());

        return ResponseEntity
                .noContent()
                .build();
    }

}
