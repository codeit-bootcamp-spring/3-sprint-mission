package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
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
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User 생성 성공"),
      @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일 또는 이름",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> create(
      @Parameter(
          description = "User 생성 정보",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserCreateRequest.class)
          )
      )
      @RequestPart UserCreateRequest userCreateRequest,

      @Parameter(
          description = "User 프로필 이미지",
          content = @Content(
              mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
              schema = @Schema(type = "array", format = "binary", implementation = MultipartFile.class)
          )
      )
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    BinaryContentCreateRequest profileImageRequest = null;

    if (profile != null) {
      profileImageRequest = resolveProfileImageRequest(profile).orElse(null);
    }

    UserResponse response = userService.create(userCreateRequest, profileImageRequest);
    return ResponseEntity.created(URI.create("/api/users/" + response.id())).body(response);
  }

  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content()),
      @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일 또는 이름",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PatchMapping("/{userId}")
  public ResponseEntity<UserResponse> update(
      @Parameter(
          description = "수정할 User 정보",
          required = true
      )
      @PathVariable UUID userId,

      @Parameter(
          description = "수정할 User 프로필 이미지"
      )
      @RequestBody UserUpdateRequest request
  ) {
    Optional<UserResponse> updated = userService.update(userId, request);
    return updated.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트 됨",
          content = @Content(schema = @Schema(implementation = UserStatus.class))),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  })
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<?> userStatusUpdate(@PathVariable UUID userId) {
    UserStatus userstatus = userStatusService.updateByUserId(userId);
    return ResponseEntity.ok(userstatus);
  }

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "User 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
  )
  @GetMapping
  public ResponseEntity<List<UserResponse>> findAll() {
    List<UserResponse> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  @Operation(summary = "User 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
      }
  )
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> delete(@PathVariable UUID userId) {
    Optional<UserResponse> deleted = userService.delete(userId);
    return deleted.map(r -> ResponseEntity.noContent().build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private Optional<BinaryContentCreateRequest> resolveProfileImageRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        );
        return Optional.of(request);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
