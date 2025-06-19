package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
@Slf4j
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponse> createReadStatus(
        @RequestBody ReadStatusCreateRequest request
    ) {
        log.info(
            "[ReadStatusController] Create read status request received. [userId={}] [channelId={}]",
            request.userId(), request.channelId());

        ReadStatusResponse created = readStatusService.create(request);

        log.debug("[ReadStatusController] Read status created. [id={}]", created.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
        @PathVariable UUID readStatusId,
        @RequestBody ReadStatusUpdateRequest request
    ) {
        log.info("[ReadStatusController] Update read status request received. [id={}]",
            readStatusId);

        ReadStatusResponse updated = readStatusService.update(readStatusId, request);

        log.debug("[ReadStatusController] Read status updated. [id={}]", updated.id());
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId) {
        log.info("[ReadStatusController] Get read statuses by userId. [userId={}]", userId);

        List<ReadStatusResponse> result = readStatusService.findAllByUserId(userId);

        log.debug("[ReadStatusController] Read statuses found. [count={}] [userId={}]",
            result.size(), userId);
        return ResponseEntity.ok(result);
    }
}
