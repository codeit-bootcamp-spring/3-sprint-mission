package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusRequestDTO readStatusRequestDTO) {
    ReadStatus createdReadStatus = readStatusService.create(readStatusRequestDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
  }

  @GetMapping(path = "/{readStatusId}")
  public ResponseEntity<ReadStatusResponseDTO> findById(@PathVariable UUID readStatusId) {
    ReadStatusResponseDTO foundReadStatus = readStatusService.findById(readStatusId);

    return ResponseEntity.status(HttpStatus.OK).body(foundReadStatus);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusResponseDTO>> findAll(
      @RequestParam(required = false) UUID userId) {
    List<ReadStatusResponseDTO> readStatuses;

    if (userId != null) {
      readStatuses = readStatusService.findAllByUserId(userId);
    } else {
      readStatuses = readStatusService.findAll();
    }

    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }

  @PatchMapping(path = "/{readStatusId}")
  public ResponseEntity<ReadStatusResponseDTO> update(@PathVariable UUID readStatusId,
      @RequestBody ReadStatusRequestDTO readStatusRequestDTO) {
    ReadStatusResponseDTO updatedReadStatus = readStatusService.update(readStatusId,
        readStatusRequestDTO);

    return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
  }

  @DeleteMapping(path = "/{readStatusId}")
  public ResponseEntity<String> deleteById(@PathVariable UUID readStatusId) {
    readStatusService.deleteById(readStatusId);

    return ResponseEntity.status(HttpStatus.OK).body("[Success]: 메시지 수신 정보 삭제 성공!");
  }
}
