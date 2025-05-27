package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(
    name = "User",
    description = "User API"
)
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(
      summary = "User 등록",
      operationId = "create",
      tags = {"User"}
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = UserCreateRequest.class))),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(schema = @Schema(hidden = true)))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)   // 파일(프로필 이미지)이 있을수도 있고 없을수도 있다.
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileReq = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User createdUser = userService.createUser(userCreateRequest, profileReq);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    // 응답에 User의 password도 포함됨. 해시화 혹은 UserDto로 응답대체
  }

  @Operation(
      summary = "전체 User 목록 조회",
      operationId = "findAll",
      tags = {"User"}
  )
  @ApiResponse(
      responseCode = "200",
      description = "User 목록 조회 성공",
      content = @Content(mediaType = "*/*",
          array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
      )
  )
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @Operation(
      summary = "User 정보 수정",
      operationId = "update"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "User 정보가 성공적으로 수정됨",
              content = @Content(schema = @Schema(implementation = User.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "user with email {newEmail} already exists"))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "User를 찾을 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "User with id {userId} not found"))
          )
      }
  )
  @Parameter(
      name = "userId",
      in = ParameterIn.PATH,
      description = "수정할 User ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @PatchMapping(
      path = "/{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<User> update(@PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @Operation(
      summary = "User 삭제",
      operationId = "delete"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "User가 성공적으로 삭제됨"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "User를 찾을 수 없음",
              content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User with id {id} not found"))
          )
      }
  )
  @Parameter(
      name = "userId",
      in = ParameterIn.PATH,
      description = "삭제할 User ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(
      summary = "User 온라인 상태 업데이트",
      operationId = "updateUserStatusByUserId"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "User 온라인 상태가 성공적으로 업데이트됨",
              content = @Content(mediaType = "*/*", schema = @Schema(implementation = UserStatus.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "해당 User의 UserStatus를 찾을 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "UserStatus with userId {userId} not found")
              )
          )
      }
  )
  @Parameter(
      name = "userId",
      in = ParameterIn.PATH,
      description = "상태를 변경할 User ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UserStatusUpdateRequest.class)
      ),
      required = true
  )
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(@PathVariable("userId") UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
  }

  // MultiPartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
      return Optional.empty();
    } else {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
      try {
        BinaryContentCreateRequest req = new BinaryContentCreateRequest(
            profile.getOriginalFilename(),
            profile.getBytes(),
            profile.getContentType());
        return Optional.of(req);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}