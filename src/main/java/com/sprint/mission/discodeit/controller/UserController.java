package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserUpdateNameRequest;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller fileName       : UserController author :
 * doungukkim date           : 2025. 5. 8. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 8.        doungukkim 최초 생성
 */
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;


    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response : OK
    @RequestMapping(
            method = RequestMethod.GET)
    public ResponseEntity<?> findAll() {
        return userService.findAllUsers();
    }

    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response : OK
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profileFile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profileFile)
                        .flatMap(this::resolveProfileRequest);
        return userService.create(request, profileRequest);
    }

    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response : OK
    @RequestMapping(path = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID userId) {
        return userService.deleteUser(userId);
    }

    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response + status : OK
    // fail response + status : OK
    @RequestMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.PATCH)
    private ResponseEntity<?> updateProfile(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile") MultipartFile profileFile) {
        return userService.updateImage(userId, request, profileFile);
    }


    // 🗣 USER STATUS에서 가져온 메서드
    // 관심사 분리를 위해선 userStatus에서 하는게 맞지 않나? 메서드가 하나라 그냥 하는건가?
    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response + status : OK
    // fail response + status : OK
    @RequestMapping(path = "/{userId}/userStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTime(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateByUserIdRequest request) {
        return userStatusService.updateByUserId(userId, request.newLastActiveAt());
    }

    //-------------------------- swagger에 없는 엔드포인트
    @RequestMapping(
            path = "/change-name",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> changeName(@RequestBody UserUpdateNameRequest request) {
        return userService.updateUser(request.userId(), request.name());
    }

    @RequestMapping(path = "/find-by-id", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUserById(@RequestBody UserFindRequest userFindRequest) {
        return userService.findUserById(userFindRequest.userId());
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
