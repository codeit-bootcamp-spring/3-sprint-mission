package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
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
 *  - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *  - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것
 * 2. 요청(Request)
 *  - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *  - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의.
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성
 *  - 응답 상태 코드 정의
 *  - 응답 데이터 정의
 *  - (옵션) 응답 헤더 정의
 * */

@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    /**
     * 신규 사용자 등록
     *
     * @param userCreateRequest 유저 생성 요청 DTO
     * @param profile           프로필 이미지
     * @return 생성된 User (HTTP 201 CREATED)
     */
    @Operation(
            summary = "User 등록",
            operationId = "create"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(examples = @ExampleObject("User with email {email} already exists")))
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @Parameter(description = "User 프로필 이미지", required = false)
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        BinaryContentCreateRequest profileRequestDTO = null;

        if (profile != null) {
            profileRequestDTO = resolveProfileRequest(profile).orElse(null);
        }

        User createdUser = userService.create(userCreateRequest, profileRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    /**
     * MultipartFile 타입의 요청값을 BinaryContentCreateReqeust 타입으로 변환
     *
     * @param profile 프로필 (MultipartFile)
     * @return 생성된 바이너리 파일 생성 요청 DTO
     */
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile.isEmpty()) {
            return Optional.empty();
        } else {
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

    /**
     * 사용자 정보 수정
     *
     * @param userId            수정할 사용자 ID
     * @param userUpdateRequest 유저 수정 요청 DTO
     * @param profile           수정할 프로필 이미지
     * @return 수정된 User (HTTP 200 OK)
     */
    @Operation(
            summary = "User 정보 수정",
            operationId = "update"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(examples = @ExampleObject("user with email {newEmail} already exists"))),
                    @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject("User with id {userId} not found")))
            }
    )
    @PatchMapping(
            path = "/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<User> update(
            @Parameter(description = "수정할 User ID", required = true)
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @Parameter(description = "수정할 User 프로필 이미지", required = false)
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        BinaryContentCreateRequest profileRequestDTO = null;

        if (profile != null) {
            profileRequestDTO = resolveProfileRequest(profile).orElse(null);
        }

        User updatedUser = userService.update(userId, userUpdateRequest, profileRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    /**
     * 사용자 삭제
     *
     * @param userId 삭제할 유저 ID
     * @return 삭제 완료 메시지 (HTTP 200 OK)
     */
    @Operation(
            summary = "User 삭제",
            operationId = "delete"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
                    @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject("User with id {id} not found")))
            }
    )
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID", required = true)
            @PathVariable UUID userId
    ) {
        userService.delete(userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * 모든 사용자 조회
     *
     * @return 조회된 전체 User 목록 (HTTP 200 OK)
     */
    @Operation(
            summary = "전체 User 목록 조회",
            operationId = "findAll"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> userDtoList = userService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtoList);
    }

    /**
     * 사용자의 온라인 상태 업데이트
     *
     * @param userId 대상 사용자 ID
     * @return 업데이트 된 UserStatus (HTTP 200 OK)
     */
    @Operation(
            summary = "User 온라인 상태 업데이트",
            operationId = "updateUserStatusByUserId"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨", content = @Content(schema = @Schema(implementation = UserStatus.class))),
                    @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음", content = @Content(examples = @ExampleObject("UserStatus with userId {userId} not found")))
            }
    )
    @PatchMapping(path = "/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
            @Parameter(description = "상태를 변경할 User ID", required = true)
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId,
                userStatusUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserStatus);
    }
}
