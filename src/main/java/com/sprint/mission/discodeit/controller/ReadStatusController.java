package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨"),
          @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함"
              , content = @Content(examples = {
              @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists")})),
          @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Channel | User with id {channelId | userId} not found")}))
      }
  )
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

  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<ReadStatusResponseDTO>> findAll(
      @Parameter(description = "조회할 User ID")
      @RequestParam(required = false) UUID userId) {
    List<ReadStatusResponseDTO> readStatuses;

    if (userId != null) {
      readStatuses = readStatusService.findAllByUserId(userId);
    } else {
      readStatuses = readStatusService.findAll();
    }

    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }

  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
          @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "ReadStatus with id {readStatusId} not found")}))
      }
  )
  @PatchMapping(path = "/{readStatusId}")
  public ResponseEntity<ReadStatusResponseDTO> update(
      @Parameter(description = "수정할 읽음 상태 ID") @PathVariable UUID readStatusId,
      @RequestBody ReadStatusRequestDTO readStatusRequestDTO) {
    ReadStatusResponseDTO updatedReadStatus = readStatusService.update(readStatusId,
        readStatusRequestDTO);

    return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
  }
}
