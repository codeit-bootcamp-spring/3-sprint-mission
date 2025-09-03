package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @GetMapping
    @Override
    public ResponseEntity<List<NotificationDto>> findAll() {
        List<NotificationDto> notificationDtos = notificationService.findAll();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(notificationDtos);
    }

    @DeleteMapping(path = "{notificationId}")
    @PreAuthorize("@authService.isOwnerOfNotification(#notificationId, authentication.principal.id)")
    @Override
    public ResponseEntity<Void> confirm(@PathVariable UUID notificationId) {
        notificationService.delete(notificationId);

        return ResponseEntity.noContent().build();
    }
}
