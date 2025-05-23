package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** API 구현 절차
 * 1. end point
 * - endpoint는 URL과 HTTP 메서드로 구성됨
 * - endpoint는 다른 API와 중복되지 않는 유일한 값으로 정의할 것
 * 2. Request
 * - 요청으로부터 어떤 값을 받아야 하는지 정의
 * - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의
 * 3. Response - view 기반이 아닌 데이터 기반 응답으로 작성 ( 논 리뷰: ViewResolver, View, Java 객체: HTTPMessageConverter)
 * - 응답 상태 코드 정의
 * - 응답 데이터 정의
 * - (옵션) 응답 헤더 정의
 */

@RequiredArgsConstructor
@RequestMapping("/api/user")
@Controller
public class UserController {

    private final UserService userService; //(BasicUserService 대신 다형성을 살렸음)
    private final UserStatusService userStatusService;

    //신규 유저 생성 요청
    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE //"multipart/form-data"
    )
    @ResponseBody
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile // profileRequestNullable 비어있는지 확인 필요.
    ){
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);
        User createUser = userService.createUser(userCreateRequest, profileRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
    }

    // 전체 유저 정보 조회 요청
    @RequestMapping(
            path = "/findAll",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<UserDto>> getUser() {
        List<UserDto> getUser = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(getUser);
    }

    // 유저 정보 수정 요청
    @RequestMapping(
            path = "/update/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE // "multipart/form-data"
    )
    @ResponseBody
    public ResponseEntity<User> updateUser(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);
        User updatedUser = userService.updateUser(userId, userUpdateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    // 유저 정보 삭제 요청
    @RequestMapping(
            path = "/delete/{userId}",
            method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 유저 온라인 상태 변경 요청
    @RequestMapping(
            path = "/onlineStatusUpdate",
            method = RequestMethod.PUT
    )
    @ResponseBody
    public ResponseEntity<String> onlineStatusUpdate(
            @RequestParam("userId") UUID userId
    ) {
        userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(Instant.now()));

        return ResponseEntity.status(HttpStatus.OK).body("[From. Server] Online 상태 변경 완료");
    }

    //MultipartFile 타입의 요청값을 BinaryContentCreateRequest으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile){
        if(profile.isEmpty()){
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어 있다면
            return Optional.empty();
        }else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes() // 예외 처리 필요해서 try/catch
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
