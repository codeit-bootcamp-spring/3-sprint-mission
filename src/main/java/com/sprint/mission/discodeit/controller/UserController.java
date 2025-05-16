package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserCreateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/* API 구현 절차
* 1. 엔드포인트(End-point)
*   - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
*   - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것.
*
* 2. 요청(Request)
*   - 요청으로부터 어떤 값을 받아야 하는지 정의.
*   -각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의.
*   - 여기서는 form 데이터를 받아오기 때문에 Body
*
* 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성.
*   - 응답 상태 코드 정의
*   - 응답 데이터 정의
*   - (옵션) 응답 헤더 정의
*       -> ResponseEntity
* */

// form 데이터는 꼭 ENC? 형태의 멀티파트로 만들어줘야함.
// 그럼 form 데이터가 이미지 파트 하나 유저 정보 파트 하나 이런식으로 날아올 것.


@RequiredArgsConstructor
@RequestMapping("/api/user")
@Controller
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 신규 유저 생성 요청
    @RequestMapping(
            path = "/create"
//            , method = RequestMethod.POST
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<UserCreateDto> createUser(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {

        // 컨트롤러는 필요하다면 값을 가공해서 서비스에게 넘겨줘야 한다.
        // 서비스는 오로지 작업만! 해야함.
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        User createdUser = userService.createUser(userCreateRequest, profileRequest);
        System.out.println(createdUser);

        UserCreateDto response = new UserCreateDto(
                createdUser.getId(),
                createdUser.getCreatedAt(),
                createdUser.getUpdatedAt(),
                createdUser.getName(),
                createdUser.getUserEmail(),
                createdUser.getProfileId()
        );

        System.out.println("response = " + response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 유저 이름 수정 요청
    @RequestMapping(path = "/update-name"
            , method = RequestMethod.PUT
    )
    @ResponseBody
    public ResponseEntity<String> updateName(
            @RequestParam UUID userId, @RequestParam String newName
    ){
        boolean success = userService.updateUserName(userId, newName);
        return success ? ResponseEntity.ok("이름이 변경되었습니다.") : ResponseEntity.badRequest().body("이름 변경 실패");
    }

    //유저 프로필 이미지 수정
    @RequestMapping(path = "/update-profile"
//            , method = RequestMethod.PUT
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<String> updateProfile(
            @RequestParam UUID userId
            , @RequestPart("update-profile") MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> request =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        boolean success = userService.updateProfileImage(userId, request);
        return success ? ResponseEntity.ok("프로필 이미지가 변경되었습니다.") : ResponseEntity.badRequest().body("프로필 변경 실패");
    }

    // 유저 전체 조회
    @RequestMapping(
            path = "/findAll"
            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 유저 단일 조회
    @RequestMapping(
            path = "/find-one"
//            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<UserDto> getUser(
            @RequestParam UUID userId
    ) {
        UserDto foundUser = userService.getUserById(userId);
        return ResponseEntity.ok(foundUser);
    }

    //유저 삭제
    @RequestMapping(
            path = "/delete"
//            , method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<String> deleteUser(
            @RequestParam UUID userId
    ){
        boolean deleted = userService.deleteUserById(userId);
        return deleted ? ResponseEntity.ok("삭제 성공") : ResponseEntity.badRequest().body("삭제 실패");
    }

    // 유저 상태 업데이트 요청
    @RequestMapping(path = "/status/update")
    @ResponseBody
    public ResponseEntity<String> updateStatus(
            @RequestBody UserStatusUpdateRequest request
    ) {
        boolean updated = userStatusService.updateUserStatus(request);
        return updated ? ResponseEntity.ok("사용자 상태가 업데이트되었습니다.") : ResponseEntity.badRequest().body("상태 업데이트 실패");
    }



//    // 단일 파일 처리
//    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
//    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
//
//        if(profile.isEmpty()) {
//            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
//            return Optional.empty();
//        } else {
//            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
//            try {
//                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
//                        profile.getOriginalFilename(),
//                        profile.getContentType(),
//                        profile.getBytes()
//                );
//                return Optional.of(binaryContentCreateRequest);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

    // 단일 파일 처리
    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {

        if (profile == null || profile.isEmpty()) {
            // 기본 이미지 경로로부터 BinaryContentCreateRequest 생성
            try {
                ClassPathResource resource = new ClassPathResource("static/images/default-avatar.png");
                byte[] bytes = resource.getInputStream().readAllBytes();

                BinaryContentCreateRequest defaultRequest = new BinaryContentCreateRequest(
                        "default-avatar.png",
                        "image/png",
                        bytes
                );
                return Optional.of(defaultRequest);
            } catch (IOException e) {
                throw new RuntimeException("기본 이미지 파일을 읽는 중 오류가 발생했습니다.", e);
            }
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
