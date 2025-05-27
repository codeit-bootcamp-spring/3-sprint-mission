package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "사용자", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;


  @Operation(
      summary = "User 등록"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "사용자가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = User.class))),
          @ApiResponse(responseCode = "400", description = "중복되는 사용자 데이터가 있습니다.", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> createUser(
      @RequestPart(value = "userCreateRequest") CreateUserRequest request,
      @Parameter(description = "사용자 프로필 이미지", required = false)
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    Optional<CreateBinaryContentRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User user = userService.create(request, profileRequest);
    System.out.println("유저 프로필 : " + user.getProfileId()); // 프로필이 있는 사용자 생성
    boolean online = userStatusService.findByUserId(user.getId()).isOnline();
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(user);

  }

  @Operation(
      summary = "사용자 목록 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "사용자 조회 성공", content = @Content(schema = @Schema(implementation = UserDTO.class))),
      }
  )
  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> findUser(
      @Parameter(description = "조회할 사용자 ID", required = true)
      @PathVariable UUID userId
  ) {
    User user = userService.find(userId);
    boolean online = userStatusService.findByUserId(user.getId())
        .isOnline();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(UserDTO.fromDomain(user, online));
  }


  @Operation(
      summary = "사용자 목록 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))))
      }
  )
  @GetMapping
  public ResponseEntity<List<UserDTO>> findAllUsers() {
    List<UserDTO> userDTOList = userService.findAll().stream()
        .map(user -> {
          boolean online = userStatusService.findByUserId(user.getId())
              .isOnline();
          return UserDTO.fromDomain(user, online);
        }).collect(Collectors.toList());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDTOList);
  }

  @Operation(
      summary = "사용자 정보 수정"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공", content = @Content(schema = @Schema(implementation = User.class))),
          @ApiResponse(responseCode = "400", description = "중복되는 사용자 데이터가 있습니다", content = @Content(schema = @Schema(hidden = true))),
          @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PatchMapping(value = "/{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> update(
      @Parameter(description = "수정할 사용자 ID", required = true)
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UpdateUserRequest updateUserRequest,
      @Parameter(description = "수정할 사용자 프로필 이미지", required = false)
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    Optional<CreateBinaryContentRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);

    User user = userService.update(userId, updateUserRequest, profileRequest);
    userStatusService.findByUserId(userId).update(Instant.now());
    boolean online = userStatusService.findByUserId(userId)
        .isOnline();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }


  @Operation(
      summary = "사용자 온라인 상태 업데이트"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "사용자 온라인 상태 업데이트 성공", content = @Content(schema = @Schema(implementation = UserDTO.class))),
          @ApiResponse(responseCode = "404", description = "해당 사용자의 상태 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PatchMapping("/{userId}/status")
  public ResponseEntity<UserDTO> updateUserStatus(
      @Parameter(description = "상태를 변경할 사용자 ID", required = true)
      @PathVariable UUID userId,
      @RequestBody UpdateUserStatusRequest updateUserStatusRequest
  ) {

    User user = userService.find(userId);
    UserStatus userStatus = userStatusService.findByUserId(userId);
    userStatus.update(updateUserStatusRequest.newLastActiveAt());
    boolean online = userStatusService.findByUserId(user.getId())
        .isOnline();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(UserDTO.fromDomain(user, online));
  }


  @Operation(
      summary = "사용자 ID 삭제"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
          @ApiResponse(responseCode = "404", description = "사용자 ID를 찾을 수 없습니다", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @DeleteMapping("/{userId}")
  public ResponseEntity<String> deleteUser(
      @Parameter(description = "삭제할 사용자 ID", required = true)
      @PathVariable UUID userId

  ) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body("사용자 ID : " + userId + " 삭제 성공");
  }

  private Optional<CreateBinaryContentRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    }
    try {
      CreateBinaryContentRequest binaryContentCreateRequest = new CreateBinaryContentRequest(
          profileFile.getOriginalFilename(),
          profileFile.getContentType(),
          profileFile.getBytes()
      );
      return Optional.of(binaryContentCreateRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
