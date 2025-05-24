package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(
    name = "User",
    description = "User API"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 신규 유저 생성 요청
    @Operation(summary = "User 등록")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
            content = @Content(mediaType = "*/*",
                examples = @ExampleObject(value = "User with email {email} already exists")))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
        @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
        @RequestPart(value = "profile", required = false) @Parameter(description = "User 프로필 이미지") MultipartFile profile
    ) {

        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

        User createdUser = userService.create(userCreateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
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
                throw new RuntimeException("프로필 처리 중 오류 발생", e);
            }
        }
    }

    // 유저 정보 수정 요청
    @Operation(summary = "User 정보 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User 수정 성공",
            content = @Content(mediaType = "*/*", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User not found")))
    })
    @PatchMapping(
        path = "/{userId}"
        , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<User> update(
        @Parameter(description = "수정할 User ID") @PathVariable UUID userId,
        @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest =
            Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        User updateedUser = userService.update(userId, userUpdateRequest, profileRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updateedUser);
    }

    // 유저 삭제 요청
    @Operation(summary = "User 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
            content = @Content(mediaType = "*/*",
                examples = @ExampleObject(value = "User not found")))
    })
    @Parameter(name = "userId", description = "삭제할 User ID", required = true)
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID userId
    ) {
        userService.delete(userId); // 이 안에 UserStatus, BinaryContent 삭제 로직 있음

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    // 유저 다건 조회 요청
    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
        content = @Content(mediaType = "*/*"))
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(
    ) {
        List<UserDto> userDtoList = userService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(userDtoList);
    }

    // 유저 상태 정보 수정 요청
    @Operation(summary = "User 온라인 상태 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User 상태 수정 성공",
            content = @Content(mediaType = "*/*", schema = @Schema(implementation = UserStatus.class))),
        @ApiResponse(responseCode = "404", description = "User 상태 정보 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "UserStatus not found")))
    })
    @PatchMapping(path = "/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatus(
        @PathVariable UUID userId,
        @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {

        System.out.println("userId = " + userId);
        System.out.println("userStatusUpdateRequest = " + userStatusUpdateRequest);

        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId,
            userStatusUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
    }
}
