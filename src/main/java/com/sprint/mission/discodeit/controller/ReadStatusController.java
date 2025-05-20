package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatus> markAsRead(
            @RequestParam UUID messageId,
            @RequestParam UUID userId
    ) {
        ReadStatus status = readStatusService.markAsRead(messageId, userId);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{messageId}/{userId}")
    public ResponseEntity<ReadStatus> updateReadStatus(
            @PathVariable UUID messageId,
            @PathVariable UUID userId
    ) {
        ReadStatus updated = readStatusService.markAsRead(messageId, userId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ReadStatus>> getReadStatusByUser(@PathVariable UUID userId) {
        List<ReadStatus> statuses = readStatusService.getByUserId(userId);
        return ResponseEntity.ok(statuses);
    }
}