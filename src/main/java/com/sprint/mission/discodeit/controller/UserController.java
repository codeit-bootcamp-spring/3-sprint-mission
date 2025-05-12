package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.User.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.User.UserDTO;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequestDTO;
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
 *  - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *  - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것
 * 2. 요청(Request)
 *  - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *  - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의.
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성
 *  - 응답 상태 코드 정의
 *  - 응답 데이터 정의
 *  - (옵션) 응답 헤더 정의
 * */

@RequiredArgsConstructor
@RequestMapping("/api/user")
@Controller
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    /**
     * 신규 사용자 등록
     *
     * @param userCreateRequestDTO 유저 생성 요청 DTO
     * @param profile 프로필 이미지
     * @return 생성된 User (HTTP 201 CREATED)
     */
    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequestDTO") UserCreateRequestDTO userCreateRequestDTO,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        // 프로필 생성 요청 DTO 설정
        BinaryContentCreateRequestDTO profileRequestDTO = null;

        if (profile != null) {
            profileRequestDTO = resolveProfileRequest(profile).orElse(null);
        }

        // 유저 생성
        User createdUser = userService.create(userCreateRequestDTO, profileRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    /** 
     * MultipartFile 타입의 요청값을 BinaryContentCreateReqeust 타입으로 변환
     *
     * @param profile 프로필 (MultipartFile)
     * @return 생성된 바이너리 파일 생성 요청 DTO
     */
    private Optional<BinaryContentCreateRequestDTO> resolveProfileRequest(MultipartFile profile){
        if (profile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequestDTO binaryContentCreateRequestDTO = new BinaryContentCreateRequestDTO(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                );
                return Optional.of(binaryContentCreateRequestDTO);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 사용자 정보 수정
     *
     * @param userId 수정할 사용자 ID
     * @param userUpdateRequestDTO 유저 수정 요청 DTO
     * @param profile 수정할 프로필 이미지
     * @return 수정된 User (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/update",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<User> update(
            @RequestParam UUID userId,
            @RequestPart("userUpdateRequestDTO") UserUpdateRequestDTO userUpdateRequestDTO,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        // 프로필 수정 요청 DTO 설정
        BinaryContentCreateRequestDTO profileRequestDTO = null;

        if (profile != null) {
            profileRequestDTO = resolveProfileRequest(profile).orElse(null);
        }

        // 유저 정보 수정
        User updatedUser = userService.update(userId,userUpdateRequestDTO,profileRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    /**
     * 사용자 삭제
     *
     * @param userId 삭제할 유저 ID
     * @return 삭제 완료 메시지 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/delete",
            method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<String> delete(
            @RequestParam UUID userId
    ) {
        // 유저 삭제
        userService.delete(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("유저가 삭제되었습니다.");
    }

    /**
     * 모든 사용자 조회
     *
     * @return 조회된 전체 User 목록 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/findAll",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        // 유저 전체 조회
        List<UserDTO> userDTOList = userService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDTOList);
    }

    /**
     * 사용자의 온라인 상태 업데이트
     *
     * @param userId 대상 사용자 ID
     * @return 업데이트 된 UserStatus (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/update/userStatus",
            method = RequestMethod.PUT
    )
    @ResponseBody
    public ResponseEntity<UserStatus> updateUserStatus(
            @RequestParam UUID userId
    ) {
        // UpdateRequestDTO 값 설정
        UserStatusUpdateRequestDTO userStatusUpdateRequestDTO = new UserStatusUpdateRequestDTO(Instant.now());

        // 업데이트
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserStatus);
    }
}
