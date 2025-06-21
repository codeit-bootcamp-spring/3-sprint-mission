package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  /* 특정 채널의 메세지 수신 정보 생성 */
  @PostMapping
  @Override
  public ResponseEntity<ReadStatusDto> create(
      @RequestBody ReadStatusCreateRequest readStatusCreateRequest
  ) {
    ReadStatusDto createdReadStatusDto = readStatusService.create(readStatusCreateRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatusDto);
  }


  /* 특정 채널의 메세지 수신 정보 수정 */
  @PatchMapping(path = "/{readStatusId}")
  @Override
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
  ) {
    ReadStatusDto updatedReadStatusDto = readStatusService.update(readStatusId,
        readStatusUpdateRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatusDto);
  }

  /* 특정 사용자의 메세지 수신 정보 조회 */
  @GetMapping
  @Override
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @RequestParam("userId") UUID userId
  ) {
    List<ReadStatusDto> ReadStatusDtoList = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ReadStatusDtoList);
  }
}
