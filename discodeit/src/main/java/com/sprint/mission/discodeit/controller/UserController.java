package com.sprint.mission.discodeit.controller;



import com.sprint.mission.discodeit.dto.binarycontent.AddBinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.user.UpdateProfileRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/create", method =  RequestMethod.POST)
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request,
                                              @RequestPart(value = "profile", required = false) MultipartFile profile) {

        CreateBinaryContentRequest profileAddDTO = null;

        if(profile != null){
            profileAddDTO = resolveProfileRequest(profile).orElse(null);
        }

        User user = userService.create(request, Optional.empty());
        boolean online = userStatusService.findByUserId(user.getId())
                .isOnline();
        return ResponseEntity.ok(UserDTO.fromDomain(user, online));
    }



    private Optional<CreateBinaryContentRequest> resolveProfileRequest(MultipartFile profile) {
        if(profile.isEmpty()){
            return Optional.empty();
        } else {
            try{
                CreateBinaryContentRequest createBinaryContentRequest = new CreateBinaryContentRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                );
                return Optional.of(createBinaryContentRequest);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
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

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public ResponseEntity<UserDTO> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        User user = userService.find(updatePasswordRequest.userId());
        user.updatePassword(updatePasswordRequest.oldPassword(), updatePasswordRequest.newPassword());
        userStatusService.findByUserId(updatePasswordRequest.userId()).update(Instant.now());
        boolean online = userStatusService.findByUserId(user.getId())
                .isOnline();
        return ResponseEntity.ok(UserDTO.fromDomain(user, online));
    }

    @RequestMapping(value = "/profile", method = RequestMethod.PUT)
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
        User user = userService.find(updateProfileRequest.userId());
        user.updateProfiledId(updateProfileRequest.newProfiledId());
        userStatusService.findByUserId(updateProfileRequest.userId()).update(Instant.now());
        boolean online = userStatusService.findByUserId(user.getId())
                .isOnline();
        return ResponseEntity.ok(UserDTO.fromDomain(user, online));
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<UserDTO> updateUserStatus(@PathVariable("id") UUID userId, @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {
        User user = userService.find(userId);
        UserStatus userStatus = userStatusService.findByUserId(userId);
        userStatus.update(updateUserStatusRequest.newLastActiveAt());
        boolean online = userStatusService.findByUserId(user.getId())
                .isOnline();
        return ResponseEntity.ok(UserDTO.fromDomain(user, online));
    }



    @RequestMapping( method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@RequestParam("id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.ok("사용자 ID : " + userId + " 삭제 성공 ");
    }


}
