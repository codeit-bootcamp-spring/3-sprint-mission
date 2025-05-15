package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReaduStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReaduStatusService readuStatusService;

  @PostMapping
  public ResponseEntity<ReadStatus> createReadStatus(
      @RequestBody CreateReadStatusRequest createReadStatusRequest) {
    ReadStatus readStatus = readuStatusService.create(createReadStatusRequest);
    return ResponseEntity.ok(readStatus);
  }

  @GetMapping
  // api/read-status로 들어오는 요청에서 {id}는 동적인 값으로 처리
  public ResponseEntity<List<ReadStatus>> findAllReadStatusByUserId(
      @RequestParam("userId") UUID userId) {
    return ResponseEntity.ok(readuStatusService.findAllByUserId(userId));
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> updateReadStatus(
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody UpdateReadStatusRequest request) {
    ReadStatus readStatus = readuStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatus);
  }
  
}
