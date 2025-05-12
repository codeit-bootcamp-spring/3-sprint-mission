package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/* API 구현 절차
 * 1. 엔드포인트(End-point)
 *   - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *   - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것.
 * 2. 요청(Request)
 *   - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *   - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의.
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성.
 *   - 응답 상태 코드 정의
 *   - 응답 데이터 정의
 *   - (옵션) 응답 헤더 정의
 * */

@RequiredArgsConstructor
@RequestMapping("/api/user")
@Controller
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 요구사항 : @RequestMapping만 사용하여 구현하기 -> method로 타입 지정( GET, POST, PUT 등 )
    // 사용자 등록( 생성 ) : POST
    // 사용자 정보 수정 : 전체( PUT ) / 일부( PATCH )
    // 사용자 삭제 : DEL
    // 전체 조회 : GET
    // 온라인 상태 변경 : PATCH  << 전체 중 일부만 바꾸는게 확실하니

    // 신규 유저 생성 요청( POST )
    @RequestMapping(
            // path || value : 요청 URL 경로 정의
            // method : 요청을 처리할 HTTP 메서드 지정( GET, POST, PUT, DELETE, PATCH 등 )
            // consumes : 데이터의 요청 MIME 타입 정의( 데이터 형식 지정 및 해당 타입만 처리하도록 제한 )
            // produces : 컨트롤러가 반환하는 데이터의 MIME 타입 정의( JSON, XML 등 )
            // headers : 헤더 값을 기반으로 요청 처리 지정( 특정 값 요구 혹은 제한 )
            // params : 요청 파라미터 값 기반으로 처리 지정( 요청 | 쿼리 파라미터를 특정 값으로 제한 )
            path = "/create"
            , method = RequestMethod.POST
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    // ? : WildCard( 어느 타입이든 처리 ) << User로 지정 시 에러 메세지 출력 불가의 이유로 변경
    public ResponseEntity<?> create(
            // @RequestPart : 하나 일 때는 value 생략 가능, 다수일 경우 이후부터 value 필요 / 복합 데이터 혹은 파일 처리
            // + value의 값이 선택적이라면 required를 false로 지정( 필수 : true( 기본 ) / 선택적 : false )
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        // 보안성 향상을 위한 비밀번호 검증 로직
        List<String> errors = PasswordValidator.getPasswordValidationErrors(userCreateRequest.getPwd());
        // 비밀번호 규칙 검증시 false 시
        if (!errors.isEmpty()) {
            // 규칙 검증 시 발생한 에러문 응답
            String errorMessage = String.join("\n", errors);
            // 응답 상태( 상태 코드 : 400 ) 및 내부 정보( 비밀번호 검증 시 발생한 에러 메세지 ) 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        // 선택적 유저 프로필 이미지 처리
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile)
                        // 선택적 프로필 이미지의 값 여부 판별 메서드 동작 후 해당 값을 저장
                        .flatMap(this::resolveProfileRequest);

        User createdUser = userService.create(userCreateRequest, profileRequest);
        // HTTP 응답 커스터마이징
        // 상태코드 : 생성됨( 201 )
        // 응답 상태( 상태 코드 : 201 ), 내부 정보( 유저 생성 DTO, 선택적 프로필 이미지 ) 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    // MultipartFile : 파일 업로드를 처리할 때 사용하는 인터페이스
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {

        if(profile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getBytes(),
                        profile.getContentType()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @RequestMapping(
            path = "/{userId}"
            // 전체적인 정보 수정이라 예상, PUT 선택
            , method = RequestMethod.PUT
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    // 사용자 정보 수정( PUT )
    public ResponseEntity<User> update(
            // 어느 사용자인지 식별
            @PathVariable UUID userId
            , @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest
            , @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        User updatedUser = userService.update(
                userId,
                userUpdateRequest,
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest)
        );
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }


    // 비밀번호 변경 전용
    @RequestMapping(
            path = "/{userId}/password"
            , method = RequestMethod.PATCH
    )
    @ResponseBody
    // 에러 메세지( String )를 출력하고자 제네릭을 와일드카드 타입으로 사용
    public ResponseEntity<?> updateByPass(
            @PathVariable UUID userId
            , @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest
    ) {

        // 새 비밀번호 규칙 검증( 대소문자, 숫자, 특수문자 포함 + 8자 이상 )
        List<String> errors = PasswordValidator.getPasswordValidationErrors(userPasswordUpdateRequest.getNewPassword());

        // 비밀번호 규칙 검증시 false 시
        if (!errors.isEmpty()) {
            // 규칙 검증 시 발생한 에러문 응답
            String errorMessage = String.join("\n", errors);
            // 응답 상태( 상태 코드 : 400 ) 및 내부 정보( 비밀번호 검증 시 발생한 에러 메세지 ) 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        // 비밀번호 규칙 검증이 ture 시, 비밀번호 변경
        User updatedUser = userService.updateByPass(userId, userPasswordUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }


    // 사용자 삭제( DEL )
    @RequestMapping(
            path = "/{userId}"
            , method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable UUID userId) {
        try {
            userService.delete(userId);
            // HTTP 상태 코드 200( ok ) 반환
            return ResponseEntity.ok("사용자 삭제에 성공했습니다");
            // 사용자를 찾을 수 없다면 예외 발생 처리
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 사용자를 찾을 수 없습니다");
        }
    }


    // 모든 사용자 조회( GET )
    @RequestMapping(
            path = "/users"
            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<UserDTO>> findAll() {
        // 모든 사용자 조회
        List<UserDTO> users = userService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }


    // 사용자 온라인 정보 수정
    @RequestMapping(
            path = "/{userId}/status"
            , method = RequestMethod.PATCH
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable UUID userId
    ) {
        // UserService와 UserStatusService를 활용한 구현
        // 온라인 상태 확인 : UserStatus의 isOnline 메서드
        UserDTO userDTO = userService.find(userId);

        // 조회 실패 시
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("messge", "User not found"));
        }

        // UserStatus 조회
        UserStatus userStatus = userStatusService.find(userId);

        // UserStatus가 존재하지않을 시
        if (userStatus == null) {
            Instant now = Instant.now();
            userStatus = new UserStatus(userId, now);
            userStatusService.create(new UserStatusCreateRequest(userId, now));
        }


        // 상태 기록
        Instant now = Instant.now();
        UserStatusUpdateRequest userStatusUpdateRequest = new UserStatusUpdateRequest(userId, now);
        UserStatus updatedStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);

        // 상태 업데이트 실패 시
        if (updatedStatus == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to update user status"));
        }

        // 현재 상태 판별
        boolean isOnline = updatedStatus.isOnline();
        String activityStatus = isOnline ? "ONLINE" : "OFFLINE";

        // 응답 메세지 구성
        Map<String, Object> response = new HashMap<>();
        response.put("유저 정보", userDTO);
        response.put("활동 상태", activityStatus);
        response.put("마지막 접속 시간", updatedStatus.getLastOnlineAt());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}