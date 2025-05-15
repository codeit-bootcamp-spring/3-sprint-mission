package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @PostMapping("/read-status")
  public ResponseEntity<ReadStatus> readStatusCreate(@RequestBody CreateReadStatusRequest request) {
    ReadStatus readStatus = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
  }

  @PutMapping("/read-status")
  public ResponseEntity<String> readStatusUpdate(@RequestBody UpdateReadStatusRequest request) {
    readStatusService.update(request);
    return ResponseEntity.status(HttpStatus.OK).body("수정완료!");
  }

  @GetMapping("/read-status")
  public ResponseEntity<List<ReadStatus>> readStatusUpdate(@RequestParam UUID userId) {
    List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
  }


}
