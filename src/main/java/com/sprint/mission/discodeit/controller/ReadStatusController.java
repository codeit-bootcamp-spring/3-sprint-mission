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

/**
 *  GET    /api/readStatuses?userId={userId}
 *  POST   /api/readStatuses
 *  PATCH  /api/readStatuses/{readStatusId}
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @GetMapping
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus created = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);   // 201
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
                                           @RequestBody ReadStatusUpdateRequest request) {
    return ResponseEntity.ok(readStatusService.update(readStatusId, request));
  }
}
