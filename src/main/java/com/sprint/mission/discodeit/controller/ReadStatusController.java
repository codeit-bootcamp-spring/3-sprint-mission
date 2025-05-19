package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest req) {
        ReadStatus readStatus = readStatusService.create(req);
        return ResponseEntity.ok(readStatus);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ReadStatus>> findByChannelId (
            @PathVariable UUID id
    ) {
        List<ReadStatus> readStatuses = readStatusService.findAllByChannelId(id);
        return ResponseEntity.ok(readStatuses);
    }

    @PutMapping("update")
    public ResponseEntity<ReadStatus> update(@RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus = readStatusService.update(readStatusUpdateRequest);
        return ResponseEntity.ok(readStatus);
    }
}