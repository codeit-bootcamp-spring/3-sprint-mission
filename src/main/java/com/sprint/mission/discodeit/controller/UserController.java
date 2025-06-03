package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/* API 구현 절차
 * 1. 엔드포인트(End-point)
 *   - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *   - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것.
 * 2. 요청(Request)
 *   - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *   - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의.
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성.
 *   - 응답 상태 코드 정의
 *   - 응답 데이터 정의
 *   - (옵션) 응답 헤더 정의
 * */

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  // 사용자 등록( 생성 ) : POST
  // 사용자 정보 수정 : 전체( PUT ) / 일부( PATCH )
  // 사용자 삭제 : DEL
  // 전체 조회 : GET
  // 온라인 상태 변경 : PATCH  << 전체 중 일부만 바꾸는게 확실하니

  @Operation(
      summary = "신규 사용자 생성",
      description = "새로운 사용자를 등록하며, 선택적으로 프로필 이미지를 포함할 수 있습니다",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "신규 사용자 생성 성공",
              content = @Content(schema = @Schema(implementation = User.class))),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 비밀번호 형식",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(value = "비밀번호는 대소문자, 숫자, 특수문자를 포함하여 최소 8자 이상이어야 합니다")
              )
          )
      }
  )
  // 신규 유저 생성 요청( POST )
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> create(
      @Valid
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    // 선택적 유저 프로필 이미지 처리
    Optional<BinaryContentCreateRequest> profileRequest =
        Optional.ofNullable(profile)
            // 선택적 프로필 이미지의 값 여부 판별 메서드 동작 후 해당 값을 저장
            .flatMap(this::resolveProfileRequest);

    User createdUser = userService.create(userCreateRequest, profileRequest);
    // HTTP 응답 커스터마이징
    // 상태코드 : 생성됨( 201 )
    // 응답 상태( 상태 코드 : 201 ), 내부 정보( 유저 생성 DTO, 선택적 프로필 이미지 ) 반환
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
  // MultipartFile : 파일 업로드를 처리할 때 사용하는 인터페이스
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
        throw new RuntimeException(e);
      }
    }
  }


  @Operation(
      summary = "사용자 정보 수정",
      description = "사용자 ID를 기반으로 사용자 정보를 수정합니다",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "사용자 정보 수정 성공",
              content = @Content(schema = @Schema(implementation = User.class))
          )
      }
  )
  @PatchMapping("/{userId}")
  // 사용자 정보 수정( PATCH )
  public ResponseEntity<User> update(
      // 어느 사용자인지 식별
      @PathVariable UUID userId
      , @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest
      , @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    User updatedUser = userService.update(
        userId,
        userUpdateRequest,
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest)
    );
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }


  // 사용자 삭제( DEL )
  @Operation(
      summary = "사용자 삭제",
      description = "ID에 해당하는 사용자를 삭제합니다",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "사용자 삭제 성공",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(value = "사용자 삭제에 성공했습니다")
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "사용자 찾을 수 없음",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(value = "해당 사용자를 찾을 수 없습니다")
              )
          )
      }
  )
  @DeleteMapping("/{userId}")
  public ResponseEntity<String> delete(@PathVariable UUID userId) {
    try {
      userService.delete(userId);
      // HTTP 상태 코드 200( ok ) 반환
      return ResponseEntity.ok("사용자 삭제에 성공했습니다");
      // 사용자를 찾을 수 없다면 예외 발생 처리
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("해당 사용자를 찾을 수 없습니다");
    }
  }


  // 모든 사용자 조회( GET )
  @GetMapping
  @Operation(
      summary = "전체 User 목록 조회",
      description = "시스템에 등록된 전체 사용자 목록을 조회합니다",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "사용자 목록 조회 성공",
              content = @Content(
                  array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
              )
          )
      }
  )
  public ResponseEntity<List<UserDto>> findAll() {
    // 모든 사용자 조회
    List<UserDto> users = userService.findAll();

    return ResponseEntity.status(HttpStatus.OK).body(users);
  }


  // 사용자 활동 상태 변경
  @Operation(
      summary = "사용자 상태 변경",
      description = "해당 사용자의 활동 상태( 온라인 / 오프라인 )를 수정합니다",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "사용자 상태 변경 성공",
              content = @Content(schema = @Schema(implementation = UserStatus.class))
          )
      }
  )
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
  ) {
    System.out.println("userId : " + userId);
    System.out.println("userStatusUpdateRequest : " + userStatusUpdateRequest);
    UserStatus updatedStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(updatedStatus);
  }

}

