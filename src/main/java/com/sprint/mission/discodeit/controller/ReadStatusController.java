package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RequestMapping("/api/read-status")
@RestController
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  // 특정 채널의 메세지 수신 정보 생성( POST )
  @Operation(summary = "채널 메세지 수신 정보 생성", description = "지정한 채널에 대해 사용자의 마지막 읽은 메세지 정보를 생성합니다")
  @ApiResponse(responseCode = "201", description = "수신 정보 생성 성공")
  @PostMapping("/channel/{channelId}/readStatuses")
  public ResponseEntity<ReadStatus> createReadStatus(
      @Parameter(description = "채널 ID", required = true)
      @PathVariable UUID channelId,
      @RequestBody ReadStatusCreateRequest request
  ) {
    // ReadStatusService의 create은 단일 파라미터임으로, RequestDTO에 대상 ID 포함
    ReadStatusCreateRequest readStatusInfo = new ReadStatusCreateRequest(
        channelId,
        request.getUserId(),
        request.getLastReadAt()
    );

    ReadStatus readStatus = readStatusService.create(readStatusInfo);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
  }


  // 특정 채널의 메세지 수신 정보 수정( PUT )
  @Operation(summary = "채널 메세지 수신 정보 수정", description = "지정한 채널에 속한 특정 읽음 정보를 수정합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "수신 정보 수정 성공"),
      @ApiResponse(responseCode = "400", description = "채널 ID가 불일치합니다")
  })
  @PutMapping("/channel/{channelId}/readStatuses/{readStatusId}")
  public ResponseEntity<ReadStatus> updateReadStatus(
      @Parameter(description = "채널 ID", required = true)
      @PathVariable UUID channelId,

      @Parameter(description = "읽음 정보 ID", required = true)
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    // 채널 경로 명시( 지정 ) 및 검증
    ReadStatus target = readStatusService.find(readStatusId);
    if (!target.getChannelId().equals(channelId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel ID가 일치하지 않습니다");
    }

    // channelId : 특정 채널에 대한 경로를 명시
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);

    return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
  }


  // 특정 사용자의 메세지 수신 정보 조회( GET )
  @Operation(summary = "사용자 읽음 정보 조회", description = "특정 사용자의 모든 메세지 읽음 정보를 조회합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "수신 정보 조회 성공"),
      @ApiResponse(responseCode = "204", description = "수신 정보가 없습니다")
  })
  @GetMapping("/user/{userId}/readStatuses")
  public ResponseEntity<List<ReadStatus>> findReadStatusByUserId(
      @Parameter(description = "사용자 ID", required = true)
      @PathVariable UUID userId
  ) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

    // 리스트가 비었을 시 HTTP 상태 코드 204 발생
    if (readStatuses.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 값이 존재하면 정상 처리 반응
    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }
}
