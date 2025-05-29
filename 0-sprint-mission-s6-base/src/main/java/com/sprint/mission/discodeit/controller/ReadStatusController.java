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
  public ResponseEntity<CustomApiResponse<ReadStatusResponse>> create(
      @RequestBody ReadStatusRequest.Create request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(CustomApiResponse.created(readStatusService.create(request)));
  }

  @PatchMapping(path = "{readStatusId}")
  @Override
  public ResponseEntity<CustomApiResponse<ReadStatusResponse>> update(
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusRequest.Update request) {
    return ResponseEntity.ok(
        CustomApiResponse.success(readStatusService.update(readStatusId, request))
    );
  }

  @GetMapping
  @Override
  public ResponseEntity<CustomApiResponse<List<ReadStatusResponse>>> findAllByUserId(
      @RequestParam("userId") UUID userId) {
    return ResponseEntity.ok(
        CustomApiResponse.success(readStatusService.findAllByUserId(userId))
    );
  }
}
