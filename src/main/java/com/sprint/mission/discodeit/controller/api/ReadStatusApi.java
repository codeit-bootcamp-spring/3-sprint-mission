package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ReadStatus.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "409",
          description = "이미 읽음 상태가 존재함",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  ResponseEntity<ReadStatusResponse> create(ReadStatusCreateRequest request);

  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "Message 읽음 상태 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class)))
  )
  ResponseEntity<List<ReadStatusResponse>> findAllByUserId(UUID userId);

  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Message 읽음 상태 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class)))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message 읽음 상태 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))
          )
      )
  })
  ResponseEntity<ReadStatusResponse> update(UUID readStatusId);
}
