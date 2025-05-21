package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User 생성 성공"),
      @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일 또는 이름",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  ResponseEntity<UserResponse> create(
      @Parameter(
          description = "User 생성 정보",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserCreateRequest.class)
          )
      )
      UserCreateRequest userCreateRequest,

      @Parameter(
          description = "User 프로필 이미지",
          content = @Content(
              mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
              schema = @Schema(type = "array", format = "binary", implementation = MultipartFile.class)
          )
      )
      MultipartFile profile
  );


  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content()),
      @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일 또는 이름",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  ResponseEntity<UserResponse> update(
      @Parameter(
          description = "수정할 User 정보",
          required = true
      )
      UUID userId,

      @Parameter(
          description = "수정할 User 프로필 이미지"
      )
      UserUpdateRequest userUpdateRequest,

      @Parameter(
          description = "User 프로필 이미지",
          content = @Content(
              mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
              schema = @Schema(type = "array", format = "binary", implementation = MultipartFile.class)
          )
      )
      MultipartFile profile
  );

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트 됨",
          content = @Content(schema = @Schema(implementation = UserStatus.class))),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  })
  ResponseEntity<UserStatus> userStatusUpdate(UUID userId);

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "User 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
  )
  ResponseEntity<List<UserResponse>> findAll();

  @Operation(summary = "User 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
      }
  )
  ResponseEntity<Void> delete(UUID userId);
}
