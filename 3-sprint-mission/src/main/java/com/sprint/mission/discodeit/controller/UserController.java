package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* API 구현 절차
* 1. 엔드포인트(End-point)
*   - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
*   - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것.
* 2. 요청(Request)
*   - 요청으로부터 어떤 값을 받아야 하는지 정의.
*   - 각 값을 HTTP 요청의 header, body 등 어느 부분에서 어떻게 받을지 정의.
* 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반(객체 반환) 응답으로 작성.
*   - 응답 상태 코드 정의
*   - 응답 데이터 정의
*   - (옵션) 응답 헤더 정의
* */

/* 반환값에 따라 사용되는 메서드
* 1. 논리 뷰 - ViewResolver, View
* 2. Java 객체 - HttpResponse .....
* */

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
@Tag(
        name = "User"
        , description = "User API"
)
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 신규 유저 생성 요청
    @Operation(
            summary = "User 등록"
            , operationId = "create"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = User.class)))
                    , @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(schema = @Schema(example = "User with email {email} already exists")))
            }
    )
    @PostMapping(
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @Parameter(
                    name = "profile"
                    , description = "User 프로필 이미지"
                    , content = @Content(
                    schema = @Schema(type="string", format="binary")
            )
            )
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        Optional<BinaryContentCreateRequest> profileDTO =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        User createdUser = userService.create(request, profileDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {

        if (profile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();

        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            BinaryContentCreateRequest binaryContentCreateDTO = null;
            try {
                binaryContentCreateDTO = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                );
                return Optional.of(binaryContentCreateDTO);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    // 유저 다건 조회
    @Operation(
            summary = "전체 User 목록 조회"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공"
                            , content = @Content(array = @ArraySchema(schema = @Schema(type = "array", implementation = UserDTO.class))))
            }
    )
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(users);
    }

    // 유저 정보 수정
    @Operation(
            summary = "User 정보 수정"
            , operationId = "update"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = User.class)))
                    , @ApiResponse(responseCode = "400", description = "같은 email 또는 username을 사용하는 User가 이미 존재함", content = @Content(schema = @Schema(example = "user with email {newEmail} already exists")))
                    , @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(schema = @Schema(example = "User with id {userId} not found")))
            }
    )
    @Parameter(
            name = "userId"
            , in = ParameterIn.PATH
            , description = "수정할 User ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @PatchMapping(
            value = "/{userId}",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<User> update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateDTO,
            @Parameter(
                    name = "profile"
                    , description = "수정할 User 프로필 이미지"
                    , schema = @Schema(type="string", format="binary")
            )
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileDTO =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        User user = userService.update(userId, userUpdateDTO, profileDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    // 유저 삭제
    @Operation(
            summary = "User 삭제"
            , operationId = "delete"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
                    , @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(schema = @Schema(example = "User with id {id} not found")))
            }
    )
    @Parameter(
            name = "userId"
            , in = ParameterIn.PATH
            , description = "삭제할 User ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @DeleteMapping(
            value = "/{userId}"
    )
    public ResponseEntity<String> delete(
            @PathVariable("userId") UUID userId
    ) {
        UserDTO user = userService.find(userId);
        String username = user.username();

        userService.delete(userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("(" + username + ") 유저를 삭제했습니다.");
    }

    // 유저 상태 업데이트
    @Operation(
            summary = "User 온라인 상태 업데이트"
            , operationId = "updateUserStatusByUserId"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨", content = @Content(schema = @Schema(implementation = UserStatus.class)))
                    , @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음", content = @Content(schema = @Schema(example = "UserStatus with userId {userId} not found")))
            }
    )
    @Parameter(
            name = "userId"
            , in = ParameterIn.PATH
            , description = "상태를 변경할 User ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @PatchMapping(
            value = "/{userId}/userStatus",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserStatus> updateStatusByUserId(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {

        System.out.println("userId = " + userId);
        System.out.println("request = " + request);

        UserStatus userStatus = userStatusService.updateByUserId(userId, request);
        System.out.println(userStatus.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userStatus);
    }


}
