package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
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
    public ResponseEntity<ReadStatusDto> createReadStatus(
        @Valid @RequestBody ReadStatusCreateRequest request) {
        ReadStatusDto newReadStatus = readStatusService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newReadStatus);
    }

    @PatchMapping("/{readStatusId}")
    @Override
    public ResponseEntity<ReadStatusDto> updateReadStatus(
        @PathVariable("readStatusId") UUID readStatusId,
        @Valid @RequestBody ReadStatusUpdateRequest request) {
        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedReadStatus);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ReadStatusDto>> findAllReadStatus(
        @RequestParam("userId") UUID userId) {
        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(readStatuses);
    }
}