package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "사용자 API")
public interface UserApi {

    @Operation(
        summary = "모든 사용자 조회",
        description = "전체 사용자 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<UserResponse>> findAllUsers();

    @Operation(
        summary = "사용자 생성",
        description = "새 사용자를 생성합니다. 프로필 이미지를 multipart로 업로드할 수 있습니다."
    )
    @ApiResponse(responseCode = "201", description = "생성 성공")
    ResponseEntity<UserResponse> createUser(
        @Parameter(description = "사용자 생성 요청 DTO", required = true)
        @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,

        @Parameter(description = "프로필 이미지 (선택)", required = false)
        @RequestPart(value = "profile", required = false) MultipartFile profile
    );

    @Operation(
        summary = "사용자 수정",
        description = "사용자 정보를 수정합니다. 프로필 이미지를 변경할 수 있습니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 성공")
    ResponseEntity<UserResponse> updateUser(
        @Parameter(description = "사용자 ID", example = "e7f3c2b1-a4d5-6789-0b1c-123456789abc")
        UUID userId,

        @Parameter(description = "사용자 수정 요청 DTO", required = true)
        @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,

        @Parameter(description = "프로필 이미지 (선택)", required = false)
        @RequestPart(value = "profile", required = false) MultipartFile profile
    );

    @Operation(
        summary = "사용자 삭제",
        description = "사용자를 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    ResponseEntity<Void> deleteUser(
        @Parameter(description = "사용자 ID", example = "e7f3c2b1-a4d5-6789-0b1c-123456789abc")
        UUID userId
    );
}
