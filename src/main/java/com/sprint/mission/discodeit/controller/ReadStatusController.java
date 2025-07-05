package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : ReadStatusController
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
@RestController
@RequestMapping("api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {
    private final ReadStatusService readStatusService;

    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> find(@RequestParam UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ReadStatusResponse> create(@Valid @RequestBody ReadStatusCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> update(
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }
}
