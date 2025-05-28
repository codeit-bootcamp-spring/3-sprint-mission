package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CodeMessageResponseDto;
import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Tag(
        name = "User",
        description = "User API"
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(
          summary = "User 등록",
          operationId = "create"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "요청이 성공적으로 처리됨", content = @Content(schema = @Schema(implementation = UserCreateRequest.class))),
          @ApiResponse(responseCode = "400", description = "같은 email 또는 USERNAME를 사용하는 User가 이미 존재함", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @Parameter(
          name = "userCreateRequest",
          description = "새롭게 등록할 사용자의 정보를 포함한 객체",
          schema = @Schema(implementation = UserCreateRequest.class)
  )
  @Parameter(
          name = "profile",
          description = "사용자 프로필 이미지",
          content = @Content(
                  mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                  schema = @Schema(type = "string", format = "binary")
          )
  )
  @PostMapping("")
  public ResponseEntity<?> create(
          @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    try {
      Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
              .flatMap(this::resolveProfileRequest);
      User createdUser = userService.create(userCreateRequest, profileRequest);

      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body(createdUser);

    } catch (IllegalArgumentException e) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.DUPLICATE_USER,
                      ResponseMessage.DUPLICATE_USER
              ));

    } catch (RuntimeException e) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.FILE_PROCESSING_ERROR,
                      ResponseMessage.FILE_PROCESSING_ERROR
              ));

    } catch (Exception e) {
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.INTERNAL_ERROR,
                      ResponseMessage.INTERNAL_SERVER_ERROR
              ));
    }
  }


  @Operation(
          summary = "User 정보 수정",
          operationId = "update"
  )
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = User.class))),
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
          name = "userUpdateRequestDto",
          schema = @Schema(implementation = UserCreateRequest.class)
  )
  @Parameter(
          name = "profile",
          description = "수정할 User 프로필 이미지",
          content = @Content(
                  mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                  schema = @Schema(type = "string", format = "binary")
          )
  )
  @PatchMapping("/{userId}")
  public ResponseEntity<?> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    try {
      Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
              .flatMap(this::resolveProfileRequest);
      User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(updatedUser);
    } catch (IllegalArgumentException e) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.DUPLICATE_USER,
                      ResponseMessage.DUPLICATE_USER
              ));

    } catch (NoSuchElementException e) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.USER_NOT_FOUND,
                      ResponseMessage.USER_NOT_FOUND
              ));
    }catch (Exception e) {
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.INTERNAL_ERROR,
                      ResponseMessage.INTERNAL_SERVER_ERROR
              ));
    }
  }

  @Operation(
          summary = "User 삭제",
          operationId = "delete"
  )
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
                  @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User with id {id} not found")))
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
  public ResponseEntity<?> delete(@PathVariable("userId") UUID userId) {
    try {
      userService.delete(userId);
      return ResponseEntity
              .status(HttpStatus.NO_CONTENT)
              .body("User가 성공적으로 삭제됨");
    } catch (NoSuchElementException e) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.USER_NOT_FOUND,
                      ResponseMessage.USER_NOT_FOUND
              ));
    }
  }

  @Operation(
          summary = "전체 User 목록 조회",
          operationId = "findAll"
  )
  @ApiResponse(
          responseCode = "200",
          description = "User 목록 조회 성공",
          content = @Content(
                  mediaType = "*/*",
                  array = @ArraySchema(
                          schema = @Schema(implementation = UserDto.class)
                  )
          )
  )
  @GetMapping("")
  public ResponseEntity<?> findAll() {
    try {
      List<UserDto> users = userService.findAll();
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(users);
    } catch (RuntimeException e) {
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.INTERNAL_ERROR,
                      ResponseMessage.INTERNAL_SERVER_ERROR
              ));
    }
  }

  @Operation(
          summary = "User 온라인 상태 업데이트",
          operationId = "updateUserStatusByUserId"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "User 온라인 상태가 성공적으로 업데이트됨",
                  content = @Content(
                          mediaType = "*/*",
                          schema = @Schema(implementation = UserStatus.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "해당 User의 UserStatus를 찾을 수 없음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "UserStatus with userId {userId} not found")
                  )
          )
  })
  @Parameter(
          name = "userId",
          description = "상태를 변경할 User ID",
          required = true,
          schema = @Schema(type = "string", format = "uuid")
  )
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<?> updateUserStatusByUserId(@PathVariable("userId") UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    try {
      UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(updatedUserStatus);
    } catch (NoSuchElementException e) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(CodeMessageResponseDto.error(
                      ResponseCode.USER_NOT_FOUND,
                      ResponseMessage.USER_NOT_FOUND
              ));
    }
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
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
}
