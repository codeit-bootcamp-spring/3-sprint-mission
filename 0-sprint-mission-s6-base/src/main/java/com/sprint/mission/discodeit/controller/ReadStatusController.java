package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.global.response.CustomApiResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping
  @Override
  public ResponseEntity<ReadStatusResponse> create(
      @RequestBody ReadStatusRequest.Create request) {
    ReadStatusResponse createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @PatchMapping(path = "{readStatusId}")
  @Override
  public ResponseEntity<ReadStatusResponse> update(
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusRequest.Update request) {
    ReadStatusResponse updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updatedReadStatus);
  }

  @GetMapping
  @Override
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
      @RequestParam("userId") UUID userId) {
    List<ReadStatusResponse> readStatusResponses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(readStatusResponses);
  }
}
