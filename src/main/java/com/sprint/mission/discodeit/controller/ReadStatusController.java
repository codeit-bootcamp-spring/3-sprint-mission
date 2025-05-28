package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatusResponseDto> create(@RequestBody ReadStatusRequestDto readStatusRequestDTO) {
    ReadStatusResponseDto createdReadStatus = readStatusService.create(readStatusRequestDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusResponseDto>> findAll(@RequestParam(required = true) UUID userId) {
    List<ReadStatusResponseDto> readStatuses;

    readStatuses = readStatusService.findAllByUserId(userId);

    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }

  @PatchMapping(path = "/{readStatusId}")
  public ResponseEntity<ReadStatusResponseDto> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateDto readStatusUpdateDTO) {
    ReadStatusResponseDto updatedReadStatus = readStatusService.update(readStatusId,
        readStatusUpdateDTO);

    return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
  }
}
