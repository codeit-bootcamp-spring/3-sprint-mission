package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;

    @PostMapping
    @Override
    public ResponseEntity<ReadStatusResponse> createReadStatus(
        @Valid @RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        ReadStatusResponse response = ReadStatusResponse.fromEntity(readStatus);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @PatchMapping("/{readStatusId}")
    @Override
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
        @PathVariable("readStatusId") UUID readStatusId,
        @Valid @RequestBody ReadStatusUpdateRequest request) {
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
        ReadStatusResponse response = ReadStatusResponse.fromEntity(updatedReadStatus);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ReadStatusResponse>> findAllReadStatus(
        @RequestParam("userId") UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

        List<ReadStatusResponse> responses = readStatuses.stream()
            .map(ReadStatusResponse::fromEntity)
            .toList();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(responses);
    }
}