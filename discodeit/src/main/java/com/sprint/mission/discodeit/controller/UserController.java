package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method =  RequestMethod.POST)
    public ResponseEntity<UserDTO> createUser(@RequestPart(value = "request") CreateUserRequest request,
                                              @RequestPart(value = "profile", required = false) MultipartFile profile
                                              ) {
        if (profile == null || profile.isEmpty()) {
            User user = userService.create(request); // 프로필 없이 사용자 생성
            boolean online = userStatusService.findByUserId(user.getId()).isOnline();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserDTO.fromDomain(user, online));
        }

        CreateBinaryContentRequest profileRequest = resolveProfileRequest(profile);

        User user = userService.create(request, profileRequest);
        System.out.println("유저 프로필 : " + user.getProfileId() );
        boolean online = userStatusService.findByUserId(user.getId()).isOnline();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserDTO.fromDomain(user, online));

    }



    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<UserDTO> findUser(@RequestParam("id") UUID userId) {
        User user = userService.find(userId);
        boolean online = userStatusService.findByUserId(user.getId())
                .isOnline();
        return ResponseEntity.ok(UserDTO.fromDomain(user, online));
    }


    @RequestMapping(value = "/findAll",method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        List<UserDTO> userDTOList = userService.findAll().stream()
                .map(user -> {
                    boolean online = userStatusService.findByUserId(user.getId())
                            .isOnline();
                    return UserDTO.fromDomain(user, online);
                }).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOList);
    }

    @RequestMapping(value = "/{userId}",method = RequestMethod.PATCH)
    public ResponseEntity<UserDTO> update(  @PathVariable("userId") UUID userId,
                                            @RequestPart UpdateUserRequest updateUserRequest,
                                            @RequestPart(value = "profile", required = false) MultipartFile profile)
    {
        if (profile == null || profile.isEmpty()) {
            User user = userService.update(userId, updateUserRequest);
            userStatusService.findByUserId(userId).update(Instant.now());
            boolean online = userStatusService.findByUserId(userId)
                    .isOnline();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(UserDTO.fromDomain(user, online));
        }

        CreateBinaryContentRequest profileRequest = resolveProfileRequest(profile);

        User user = userService.update(userId, updateUserRequest, profileRequest);
        userStatusService.findByUserId(userId).update(Instant.now());
        boolean online = userStatusService.findByUserId(userId)
                .isOnline();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserDTO.fromDomain(user, online));
    }


    @RequestMapping(value = "/{userId}")
    public ResponseEntity<UserDTO> updateUserStatus(@PathVariable("userId") UUID userId, @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {
        User user = userService.find(userId);
        UserStatus userStatus = userStatusService.findByUserId(userId);
        userStatus.update(updateUserStatusRequest.newLastActiveAt());
        boolean online = userStatusService.findByUserId(user.getId())
                .isOnline();
        return ResponseEntity.ok(UserDTO.fromDomain(user, online));
    }



    @RequestMapping(value = "/{userId}",method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.ok("사용자 ID : " + userId + " 삭제 성공 ");
    }

    private CreateBinaryContentRequest resolveProfileRequest(MultipartFile profileFile) {
        try {
            CreateBinaryContentRequest binaryContentCreateRequest = new CreateBinaryContentRequest(
                    profileFile.getOriginalFilename(),
                    profileFile.getContentType(),
                    profileFile.getBytes()
            );
            return binaryContentCreateRequest;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
