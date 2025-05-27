package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

  /* 유저 생성 */
  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "User가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = User.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "User with email {email} already exists"))
      ),
  })
  public ResponseEntity<User> create(
      @Parameter(
          description = "User 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
          required = true
      ) UserCreateRequest userCreateRequest,
      @Parameter(
          description = "User 프로필 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) MultipartFile profile
  );

  /* 유저 수정 */
  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = User.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("User with id {userId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject("user with email {newEmail} already exists"))
      )
  })
  public ResponseEntity<User> update(
      @Parameter(description = "수정할 User ID", required = true) UUID userId,
      @Parameter(description = "수정할 User 정보", required = true) UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지") MultipartFile profile
  );

  /* 유저 삭제 */
  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "User가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {id} not found"))
      )
  })
  @DeleteMapping(path = "/{userId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 User ID", required = true) UUID userId
  );

  /* 유저 조회 All */
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      )
  })
  public ResponseEntity<List<UserDto>> findAll();

  /* 유저 상태 업데이트 */
  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(schema = @Schema(implementation = UserStatus.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found"))
      )
  })
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @Parameter(description = "상태를 변경할 User ID") UUID userId,
      @Parameter(description = "변경할 User 온라인 상태 정보") UserStatusUpdateRequest request
  );
}
