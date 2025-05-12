package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.user.*;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadResponse;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : UserController
 * author         : doungukkim
 * date           : 2025. 5. 8.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 8.        doungukkim       최초 생성
 */
@Controller
@RequestMapping("api/user/*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
//    private final UserStatusService userStatusService;

    /*
        api 구현 절차
        1. 엔드포인트(end-point)
        - url과 http 메서드로 구성
        - 엔드포인트는 다른 api와 겹치지 않는(중복되지 않는) 뉴일한 값으로 정의할 것
        2. 요청(request)
        - 요청으로부터 어떤 값을 받아야 하는지 정의
        - 각 값을 http요청의 header, body등 어느 부분에서 어떻게 받을지 정의.
        3. 응답(response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성
        - 응답 상태 코드 정의
        - 응답 데이터 정의
        - (옵션) 응답 헤더 정의
     */
    @ResponseBody
    @RequestMapping(
            path = "/create"
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profileFile
    ) {
        // 새로운 dto 만들어서 service로 보낸다.
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profileFile)
                        .flatMap(this::resolveProfileRequest);

        UserCreateResponse userCreateResponse = userService.create(request, profileRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreateResponse);
    }

    @RequestMapping(
            path = "/changeName",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> changeName(@RequestBody UserUpdateNameRequest request) {
        UUID uuid = request.userId();
        return userService.updateUser(uuid, request.name());
    }

    @RequestMapping(path = "/findById", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUserById(@RequestBody UesrFindRequest userFindRequest) {
        UUID uuid = Objects.requireNonNull(userFindRequest.userId());
        return userService.findUserById(uuid);
    }

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> findAll(){
        return userService.findAllUsers();
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profileFile.getOriginalFilename(),
                        profileFile.getContentType(),
                        profileFile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @RequestMapping(path = "/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@RequestBody UserDeleteRequest request) {
        UUID uuid = request.userId();
        userService.deleteUser(uuid);
        return ResponseEntity.ok("삭제 성공");
    }
}
