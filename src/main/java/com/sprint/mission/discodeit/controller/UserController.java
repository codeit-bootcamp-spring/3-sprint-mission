package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 신규 유저 생성 요청
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
        @RequestPart("userCreateRequest")
        @Parameter(description = "User 생성 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
        UserCreateRequest userCreateRequest,
        @RequestPart(value = "profile", required = false)
        @Parameter(description = "User 프로필 이미지", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
        MultipartFile profile
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
    @Parameter(name = "userId", description = "삭제할 User ID", required = true)
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID userId
    ) {
        userService.delete(userId); // 이 안에 UserStatus, BinaryContent 삭제 로직 있음

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    // 유저 다건 조회 요청
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> userDtoList = userService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(userDtoList);
    }

    // 유저 상태 정보 수정 요청
    @Operation(summary = "User 온라인 상태 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User 상태 수정 성공",
            content = @Content(schema = @Schema(implementation = UserStatus.class))),
        @ApiResponse(responseCode = "404", description = "User 상태 정보 없음",
            content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
    })
    @PatchMapping(path = "/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatus(
        @PathVariable
        @Parameter(description = "상태를 변경할 User iD") UUID userId,
        @RequestBody
        @Parameter(description = "변경할 User 온라인 상태 정보") UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId,
            userStatusUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
    }
}
