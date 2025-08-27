package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
public interface UserApi {

    // 신규 유저 생성 요청
    @Operation(summary = "User 등록")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
            content = @Content(examples = @ExampleObject(value = "User with email {email} already exists")))
    })
    ResponseEntity<UserDto> create(
        @RequestPart("userCreateRequest")
        @Parameter(description = "User 생성 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
        UserCreateRequest userCreateRequest,
        @RequestPart(value = "profile", required = false)
        @Parameter(description = "User 프로필 이미지", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
        MultipartFile profile
    );

    // 유저 정보 수정 요청
    @Operation(summary = "User 정보 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User 수정 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "User not found"))),
        @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
            content = @Content(examples = @ExampleObject("user with email {newEmail} already exists")))
    })
    @PatchMapping(
        path = "/{userId}"
        , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<UserDto> update(
        @Parameter(description = "수정할 User ID") @PathVariable UUID userId,
        @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    );

    // 유저 삭제 요청
    @Operation(summary = "User 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
    })
    @Parameter(name = "userId", description = "삭제할 User ID", required = true)
    @DeleteMapping(path = "/{userId}")
    ResponseEntity<Void> delete(
        @PathVariable UUID userId
    );

    // 유저 다건 조회 요청
    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
    @GetMapping
    ResponseEntity<List<UserDto>> findAll();
}
