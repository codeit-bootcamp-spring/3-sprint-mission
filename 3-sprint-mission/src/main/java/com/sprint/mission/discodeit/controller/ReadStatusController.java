package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusAPI;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
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
@RequestMapping("/api/readStatuses")
@RestController
@Slf4j
public class ReadStatusController implements ReadStatusAPI {

  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatusDto> create(
      @RequestBody ReadStatusCreateRequest request
  ) {
    log.info("메시지 읽음 상태 생성 요청 userId={}, channelId={}", request.userId(), request.channelId());

    ReadStatusDto readStatus = readStatusService.create(request);
    log.info("메시지 읽음 상태 생성 완료 readStatusId={}", readStatus.id());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(readStatus);
  }

  @PatchMapping(
      value = "/{readStatusId}"
  )
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest readStatusUpdateDto
  ) {
    log.info("메시지 읽음 상태 수정 요청 readStatusId={}, request={}", readStatusId, readStatusUpdateDto);

    ReadStatusDto readStatusUpdate = readStatusService.update(readStatusId, readStatusUpdateDto);
    log.info("메시지 읽음 상태 수정 완료 readStatusId={}", readStatusUpdate.id());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatusUpdate);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @RequestParam("userId") UUID userId
  ) {
//        List<ReadStatus> readStatus = readStatusService.findAll();
    List<ReadStatusDto> readStatusDto = readStatusService.findAllByUserId(userId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatusDto);
  }
}
