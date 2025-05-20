package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReaduStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "읽음 상태", description = "읽음 상태 API")
@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReaduStatusService readuStatusService;

  @Operation(
      summary = "메시지 읽음 상태 생성"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "읽음 상태 생성 성공", content = @Content(schema = @Schema(implementation = ReadStatus.class))),
          @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함", content = @Content(schema = @Schema(hidden = true))),
          @ApiResponse(responseCode = "404", description = "채널 또는 사용자를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PostMapping
  public ResponseEntity<ReadStatus> createReadStatus(
      @RequestBody CreateReadStatusRequest createReadStatusRequest
  ) {
    ReadStatus readStatus = readuStatusService.create(createReadStatusRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(readStatus);
  }

  @Operation(
      summary = "사용자의 메시지 읽음 상태 목록 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
              content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))))
      }
  )
  @GetMapping
  // api/read-status로 들어오는 요청에서 {id}는 동적인 값으로 처리
  public ResponseEntity<List<ReadStatus>> findAllReadStatusByUserId(
      @Parameter(description = "조회할 User ID", required = true)
      @RequestParam("userId") UUID userId
  ) {

    List<ReadStatus> readStatusList = readuStatusService.findAllByUserId(userId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatusList);
  }

  @Operation(
      summary = "메시지 읽음 상태 수정"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "메시지 읽음 상태 수정", content = @Content(schema = @Schema(implementation = ReadStatus.class))),
          @ApiResponse(responseCode = "404", description = "메시지 읽음 상태를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> updateReadStatus(
      @Parameter(description = "수정할 읽음 상태 ID", required = true)
      @PathVariable UUID readStatusId,
      @RequestBody UpdateReadStatusRequest request
  ) {

    ReadStatus readStatus = readuStatusService.update(readStatusId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatus);
  }

}
