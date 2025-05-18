package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.*;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* API 구현 절차
* 1. 엔드포인트(End-point)
*   - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
*   - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것.
* 2. 요청(Request)
*   - 요청으로부터 어떤 값을 받아야 하는지 정의.
*   - 각 값을 HTTP 요청의 header, body 등 어느 부분에서 어떻게 받을지 정의.
* 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반(객체 반환) 응답으로 작성.
*   - 응답 상태 코드 정의
*   - 응답 데이터 정의
*   - (옵션) 응답 헤더 정의
* */

/* 반환값에 따라 사용되는 메서드
* 1. 논리 뷰 - ViewResolver, View
* 2. Java 객체 - HttpResponse .....
* */

@RequiredArgsConstructor
@RequestMapping("/api/user")
@ResponseBody
@Controller
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 신규 유저 생성 요청
    @RequestMapping(
            value = "/create"
//            path = {"/create", "/modify", "/delete"}
//            , method = RequestMethod.POST //이러면 POST 요청만 얘가 받게됨.
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<User> create(
            @RequestPart("userCreateDTO") UserCreateRequest userCreateDTO,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        Optional<BinaryContentCreateRequest> profileDTO =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        User createdUser = userService.create(userCreateDTO, profileDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {

        if (profile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();

        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            BinaryContentCreateRequest binaryContentCreateDTO = null;
            try {
                binaryContentCreateDTO = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                );
                return Optional.of(binaryContentCreateDTO);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    // 유저 다건 조회
    @RequestMapping(
            value = "/findAll",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(users);
    }

    // 유저 정보 수정
    @RequestMapping(
            value = "/update",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> update(
            @RequestParam UUID userId,
            @RequestPart("userUpdateDTO") UserUpdateRequest userUpdateDTO,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileDTO =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        User user = userService.update(userId, userUpdateDTO, profileDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    // 유저 삭제
    @RequestMapping(
            value = "/delete"
            , method = RequestMethod.DELETE
//            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> delete(
            @RequestParam UUID userId
    ) {
        UserDTO user = userService.find(userId);
        String username = user.username();
        String name = user.name();

        userService.delete(userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(name + "(" + username + ") 유저를 삭제했습니다.");
    }

    // 유저 상태 업데이트
    @RequestMapping(
            value = "/update-status",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserStatus> updateStatus(
            @RequestParam UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateDTO
    ) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateDTO);
        System.out.println(userStatus.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userStatus);
    }


}
