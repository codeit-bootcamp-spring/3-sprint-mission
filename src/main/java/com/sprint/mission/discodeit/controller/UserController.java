package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * API 구현 절차
 * 1. end point
 * - endpoint는 URL과 HTTP 메서드로 구성됨
 * - endpoint는 다른 API와 중복되지 않는 유일한 값으로 정의할 것
 * 2. Request
 * - 요청으로부터 어떤 값을 받아야 하는지 정의
 * - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의
 * 3. Response - view 기반이 아닌 데이터 기반 응답으로 작성 ( 논 리뷰: ViewResolver, View, Java 객체:
 * HTTPMessageConverter)
 * - 응답 상태 코드 정의
 * - 응답 데이터 정의
 * - (옵션) 응답 헤더 정의
 */

@RequiredArgsConstructor
@RequestMapping("/api/users")
@Controller
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 신규 유저 생성 요청
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE // "multipart/form-data"
    )
    @ResponseBody
    public ResponseEntity<?> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        try {
            Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                    .flatMap(this::resolveProfileRequest);

            User createdUser = userService.createUser(userCreateRequest, profileRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalStateException e) { // API 스펙: User with email {email} or username {username} already exists
                                            // (400)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create user: " + e.getMessage());
        }
    }

    // 전체 유저 정보 조회 요청
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 유저 정보 수정 요청
    @RequestMapping(path = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateUser(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {
        try {
            Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                    .flatMap(this::resolveProfileRequest);
            User updatedUser = userService.updateUser(userId, userUpdateRequest, profileRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) { // API 스펙: User with id {userId} not found (404)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id " + userId + " not found");
        } catch (IllegalStateException e) { // API 스펙: user with email {newEmail} or username {newUsername} already
                                            // exists (400)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 유저 정보 삭제 요청
    @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable("userId") UUID userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) { // API 스펙: User with id {id} not found (404)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id " + userId + " not found");
        }
    }

    // 유저 온라인 상태 변경 요청
    @RequestMapping(path = "/{userId}/userStatus", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateUserStatusByUserId(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest request) {
        try {
            UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
            return ResponseEntity.ok(updatedUserStatus);
        } catch (NoSuchElementException e) { // API 스펙: UserStatus with userId {userId} not found (404)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserStatus with userId " + userId + " not found");
        }
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile == null || profile.isEmpty()) {
            return Optional.empty();
        }
        try {
            BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes());
            return Optional.of(request);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process profile image: " + e.getMessage(), e);
        }
    }
}
