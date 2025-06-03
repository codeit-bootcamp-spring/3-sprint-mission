package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "User 목록 조회 성공",
      content = @Content(
          mediaType = "*/*",
          array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
      )
  )
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "User 등록")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "User가 성공적으로 생성됨",
          content = @Content(mediaType = "*/*", schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "User with email {email} already exists")
          )
      )
  })
  ResponseEntity<UserDto> create(
      UserCreateRequest userCreateRequest, //text
      MultipartFile profile //imag&file
  );

  @Operation(summary = "User 정보 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User 정보가 성공적으로 수정됨",
          content = @Content(mediaType = "*/*", schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "user with email {newEmail} already exists")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "User with id {userId} not found")
          )
      )
  })
  ResponseEntity<UserDto> update(
      @Parameter(
          name = "userId",
          description = "수정할 User ID",
          required = true) @PathVariable("userId") UUID userId,
      UserUpdateRequest userUpdateRequest,
      MultipartFile profile);

  @Operation(summary = "User 삭제")
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "User가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "User with id {id} not found")
          )
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(
          name = "userId",
          description = "삭제할 User ID",
          required = true) UUID userId
  );

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(schema = @Schema(implementation = UserStatusDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "UserStatus with userId {userId} not found")
          )
      )
  })
  ResponseEntity<UserStatusDto> userStatusUpdate(
      @Parameter(name = "userId",
          description = "상태를 변경할 UserID",
          required = true) UUID userId,
      @Parameter(description = "변경할 User 온라인 상태 정보") UserStatusUpdateRequest request
  );
}
