package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatus")
public class ReadStatusController {
  private final ReadStatusService readStatusService;

  // 특정 채널의 메시지 수신 정보를 생성 - private채널 생성 시 유저별 readStatus가 생성됨
  @RequestMapping("/create")
  public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus readStatus = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
  }

  // 특정 채널의 메시지 수신 정보를 수정
  @RequestMapping("/update")
  public ResponseEntity<ReadStatus> updateReadStatus(@RequestParam("readStatusId") UUID readStatusId,
                                                     @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
  }

  // 특정 사용자의 메시지 수신 정보를 조회
  @RequestMapping("/findReadStatus")
  public ResponseEntity<List<ReadStatus>> findAllReadStatus(@RequestParam("userId") UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }

}
