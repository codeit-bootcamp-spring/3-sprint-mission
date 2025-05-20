package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping("/channels/{channelId}/read-status")
    public ResponseEntity<ReadStatus> create(@PathVariable UUID channelId,
                                             @RequestBody ReadStatusCreateRequest request) {
        // channelId는 request 안에 포함되어야 함
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
    }

    @PutMapping("/read-status/{id}")
    public ResponseEntity<ReadStatus> update(@PathVariable UUID id,
                                             @RequestBody ReadStatusUpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(id, request));
    }

    @GetMapping("/users/{userId}/read-status")
    public ResponseEntity<List<ReadStatus>> findByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
