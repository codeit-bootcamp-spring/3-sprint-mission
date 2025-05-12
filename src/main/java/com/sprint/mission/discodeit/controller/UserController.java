package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.Dto.user.UserUpdateNameRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : UserController
 * author         : doungukkim
 * date           : 2025. 5. 8.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 8.        doungukkim       최초 생성
 */
@Controller
@RequestMapping("api/user/*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseBody
    @RequestMapping(
            path = "/create",
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

    @ResponseBody
    @RequestMapping(path = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<?> updateProfile(
            @RequestPart("profileUpdateRequest") ProfileUploadRequest request,
            @RequestPart(value = "profile") MultipartFile profileFile) {
        return userService.updateImage(request, profileFile);
    }

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

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> findAll(){
        return userService.findAllUsers();
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@RequestBody UserDeleteRequest request) {
        return userService.deleteUser(request.userId());

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
