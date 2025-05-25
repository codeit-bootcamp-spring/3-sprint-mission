package com.sprint.mission.discodeit.Controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;


    @RequestMapping(
            path = "create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        if(profile != null && profile.getSize()>0){
            BinaryContentCreateRequest portraitRequest = resolveProfileRequest(profile);
            User createdUser = userService.create(userCreateRequest, portraitRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } else {
            User createdUser = userService.create(userCreateRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
    }
    @RequestMapping(
            path = "update",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<User> update(
            @RequestParam("userId") UUID userId,
            @RequestPart(value = "userUpdateRequest", required = false) UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        User updatedUser = null;
        if(userUpdateRequest != null) {
            updatedUser = userService.update(userId, userUpdateRequest);
        }
        if(profile != null && profile.getSize()>0) {
            BinaryContentCreateRequest portraitRequest = resolveProfileRequest(profile);
            updatedUser = userService.update(userId, portraitRequest);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
    }
    @RequestMapping(path = "delete")
    public ResponseEntity<Void> delete(@RequestParam("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @RequestMapping(path = "findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
    @RequestMapping(path = "updateUserStatusByUserId")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(@RequestParam("userId") UUID userId, @RequestBody UserStatusUpdateRequest request) {
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId,request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
    }


    private BinaryContentCreateRequest resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return null;
        } else {
            try {
                return new BinaryContentCreateRequest(
                        profileFile.getOriginalFilename(),
                        profileFile.getContentType(),
                        profileFile.getBytes()
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
