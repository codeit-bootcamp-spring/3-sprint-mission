package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel | User with id {channelId | userId} not found"))),
      @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함", content = @Content(schema = @Schema(example = "ReadStatus with userId {userId} and channelId {channelId} already exists"))),
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ReadStatus.class)))
  })
  @PostMapping("/readStatuses")
  public ResponseEntity<ReadStatus> create_1(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus readStatus = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
  }

  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = ReadStatus.class))),
      @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음", content = @Content(schema = @Schema(example = "ReadStatus with id {readStatusId} not found"))),
  })
  @Parameter(name = "readStatusId", description = "수정할 읽음 상태 ID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @PatchMapping("/readStatuses/{readStatusId}")
  public ResponseEntity<String> update_1(@PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    readStatusService.update(readStatusId, request);
    return ResponseEntity.status(HttpStatus.OK).body("수정완료!");
  }

  @Operation(summary = "User의 읽음 상태 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class)))),
  })
  @Parameter(name = "userId", description = "조회할 USER ID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @GetMapping("/readStatuses")
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId) {
    List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
  }


}
