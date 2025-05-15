package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserResponse;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        User created = userService.create(request, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<UserResponse> find(@PathVariable("id") UUID id) {
        UserFindRequest request = UserFindRequest.builder().userId(id).build();
        return ResponseEntity.ok(userService.find(request));
    }

    @RequestMapping(
            method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<User> update(
            @PathVariable("id") UUID id,
            @RequestPart("updateRequest") UserUpdateRequest updateRequest,
            @RequestPart(value = "newProfile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        return ResponseEntity.ok(userService.update(updateRequest, profileRequest));
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile == null || profile.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류 발생", e);
        }
    }

    @RequestMapping(
            path = "/api/user/findAll",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<UserResponse>> findAllUserResponse() {
        List<UserResponse> result = userService.findAll();
        return ResponseEntity.ok(result);
    }

}