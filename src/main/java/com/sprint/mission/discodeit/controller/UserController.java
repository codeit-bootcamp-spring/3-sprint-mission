package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* API 구현 절차
 * 1. 엔드포인트(End-point)
 *  - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *  - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것.
 * 2. 요청(Request)
 *  - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *  - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성.
 *  - 응답 상태 코드 정의
 *  - 응답 데이터 정의
 *  - (옵션) 응답 헤더 정의
 * */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    //신규 유저 생성 요청
    @RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<User> create(@RequestPart("createUserRequest") CreateUserRequest createUserRequest, @RequestPart(value = "profile", required = false) MultipartFile profile) {
        Optional<CreateBinaryContentRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        User createdUser = userService.create(createUserRequest, profileRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    //MultipartFile 타입의 요청값을 CreateBinaryContentRequest 타입으로 변환하기 위한 메서드
    private Optional<CreateBinaryContentRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile.isEmpty()) {
            //컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            //컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                CreateBinaryContentRequest createBinaryContentRequest = new CreateBinaryContentRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                );
                return Optional.of(createBinaryContentRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //유저 조회 요청
    @RequestMapping(path = "/find", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<UserDto> read(@RequestParam UUID userId) {
        UserDto findUserRespond = userService.find(userId);
        return ResponseEntity.status(HttpStatus.OK).body(findUserRespond);
    }

    //유저 전체 조회 요청
    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> all = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }

    //유저 수정 요청
    @RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> update(@RequestPart("updateUserRequest") UpdateUserRequest updateUserRequest, @RequestPart(value = "profile", required = false) MultipartFile profile) {
        Optional<CreateBinaryContentRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        userService.update(updateUserRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.OK).body("유저 데이터 수정 성공");
    }

    //유저 삭제 요청
    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> delete(@RequestParam UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).body("유저 데이터 삭제 성공");
    }

    //유저 온라인상태 업데이트
    @RequestMapping(path = "/online", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserStatus> updateOnlineStatus(@RequestParam UUID userId){
        UpdateUserStatusRequest updateUserStatusRequest = new UpdateUserStatusRequest(null,userId);
        userStatusService.updateByUserId(updateUserStatusRequest);
        UserStatus userStatus = userStatusService.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userStatus);
    }
}
