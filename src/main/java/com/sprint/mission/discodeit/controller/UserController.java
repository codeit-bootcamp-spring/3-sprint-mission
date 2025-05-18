package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/* API 구현 절차
    1. 엔드포인트(End-point)
     - 엔드포인트는 URL과 HTTP 메서드로 구성됨
     - 엔드포인트는 다른 API와 겹치지 않는 유일한 값으로 정의할것.
     2. 요청
     - 요청으로부터 어떤 값을 받아야하는지 정의
     - 각 값을 HTTP 요청의 Header, Body 등 어떤 부분에서 어떻게 받을지 정의.
     3. 응답 ( 뷰 기반이 아닌 데이터 기반 응답으로 작성
     - 응답 상태 코드 정의
     - 응답 데이터 정의
     -(옵션) 응답 헤더 정의
 */
@RestController
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/api")
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;


  /* 유저 생성 */
  //FIXME : api-docs랑 다름
  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(examples = {
          @ExampleObject(value = "User with email {email} already exists")}))

  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = {
          @Content(mediaType = "application/json", schema = @Schema(
              type = "object",
              requiredProperties = {"UserCreateRequest"},
              implementation = UserCreateRequest.class)
          ),
          @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary", description = "User 프로필 이미지")),
      }
  )
  @PostMapping(path = "users", consumes = {
      MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity.created(URI.create(createdUser.getId().toString())).body(createdUser);
//    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  /* 유저 수정 */
  @PutMapping(path = "users/update/{userId}", consumes = {
      MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> update(
      @PathVariable String userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest updateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(parseStringToUuid(userId), updateRequest, profileRequest);
    return ResponseEntity.ok().body(updatedUser);
  }

  /* 유저 삭제 */
  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "User with id {id} not found")}))
  })
  @Parameter(
      name = "userId",
      description = "삭제할 User ID",
      required = true,
      content = @Content(schema = @Schema(type = "string", format = "UUID")
      )
  )
  @DeleteMapping(path = "users/{userId}")
  public ResponseEntity<Void> delete(
      @PathVariable String userId
  ) {
    userService.delete(parseStringToUuid(userId));
    //TODO : 204번으로 리턴
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /* 유저 조회 All */
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
  })
  @GetMapping(path = "users")
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    System.out.println(users.toString());
    return ResponseEntity.ok().body(users);
  }

  /* 유저 상태 업데이트 */
  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨", content = @Content(schema = @Schema(implementation = UserStatus.class))),
      @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "UserStatus with userId {userId} not found")}))
  })
  @Parameter(
      name = "userId",
      description = "상태를 변경할 User ID",
      required = true,
      content = @Content(schema = @Schema(type = "string", format = "uuid")
      )
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = UserStatusUpdateRequest.class))
  )
  @PatchMapping(path = "/users/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable String userId,
      @RequestBody UserStatusUpdateRequest userUpdateRequest
  ) {
    UserStatus userStatus = userStatusService.updateByUserId(parseStringToUuid(userId),
        userUpdateRequest);
    return ResponseEntity.ok().body(userStatus);
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* MultipartFile 타입 -> BinaryContentCreateRequest 타입으로 변경 */
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profile.getOriginalFilename(), profile.getContentType(), profile.getBytes());
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException();
      }
    }
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* String 타입 -> UUID 타입으로 변경 */
  private UUID parseStringToUuid(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
    }
  }

}
