package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readStatus.JpaReadStatusResponse;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.unit.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> find(@RequestParam UUID userId) {
        List<JpaReadStatusResponse> allByUserId = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(allByUserId);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ReadStatusCreateRequest request) {
        JpaReadStatusResponse response = readStatusService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<?> update(
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request) {
        JpaReadStatusResponse update = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(update);
    }
}
