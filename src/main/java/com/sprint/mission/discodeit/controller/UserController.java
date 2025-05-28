package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
@ResponseBody
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @Operation(summary = "사용자 생성")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        User newUser = userService.create(userCreateRequest, profileRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newUser);
    }

    @Operation(summary = "전체 사용자 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/findAll")
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = userService.getAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @Operation(summary = "사용자 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "사용자의 userState 상태 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @RequestMapping(path = "updateUserStatusByUserId")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(@RequestParam("userId") UUID userId,
                                                               @RequestBody UserStatusUpdateRequest request) {
        System.out.println("[DEBUG] updateUserStatusByUserId == null? " + (userId == null));
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserStatus);
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
