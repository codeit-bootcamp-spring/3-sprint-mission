package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

/* API 구현 절차
 * 1. 엔드포인트(End-Point)
 *  - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *  - 엔드포인트는 다른 API와 겹치지 않는 (중복되지 않는) 유일한 값으로 정의할 것.
 * 2. 요청(Request)
 *  - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *  -  각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의.
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성.
 *  - 응답 상태 코드 정의
 *  - 응답 데이터 정의
 *  - (옵션) 응답 헤더 정의*/

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Override
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {

    List<UserDto> users = userService.findAll();

    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @Override
  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE // 바이너리와 아닌 것이 같이 들어올 수 있으므로 멀티파트로~
  )
  public ResponseEntity<UserDto> create(
//      @Valid
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest, //text
      @RequestPart(value = "profile", required = false) MultipartFile profile //imag&file
  ) {
    Optional<BinaryContentCreateRequest> profileRequest =
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

    UserDto createdUser = userService.create(userCreateRequest, profileRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @Override
  @PatchMapping(
      path = "/{userId}"
      , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") UUID userId,
//      @Valid
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    Optional<BinaryContentCreateRequest> profileRequest =
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

    UserDto updatedUSer = userService.update(userId, userUpdateRequest, profileRequest);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUSer);
  }


  @Override
  @DeleteMapping(path = "/{userId}")
  public ResponseEntity<Void> delete(
      @Parameter(
          name = "userId",
          description = "삭제할 User ID",
          required = true) @PathVariable("userId") UUID userId
  ) {
    userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }


  @Override
  @PatchMapping(path = "/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> userStatusUpdate(
      @PathVariable("userId") UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);

  }


  //MultipartFile 타입의 요청값을 BinaryContentRequest 타입으로 변환하기 위한 메서드
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
      return Optional.empty();
    } else {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(
            "An error occurred while processing the profile image." + e.getMessage(), e);
      }
    }
  }

}