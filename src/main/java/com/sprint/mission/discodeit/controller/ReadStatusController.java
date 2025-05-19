package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
        return ResponseEntity.ok(readStatusService.create(request));
    }

    @PatchMapping(path = "/{readStatusId}")
    public ResponseEntity<ReadStatus> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}