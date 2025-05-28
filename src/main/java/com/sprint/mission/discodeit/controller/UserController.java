package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.util.FileConverter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/* API 구현 절차
 * 1. 엔드포인트(End-Point)
 *      - 엔드포인트는 URL과 HTTP 메서드로 구성됨
 *      - 엔드포인트는 다른 API와 겹치지 않는 유일한 값으로 정의할 것
 * 2. 요청(Request)
 *      - 요청으로부터 어떤 값을 받아야하는지 정의
 *      - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의
 * 3. 응답(Response) -> 뷰 기반이 아닌 데이터 기반 응답으로 작성
 *      - 응답 상태 코드 정의
 *      - 응답 데이터 정의
 *      - (옵션) 응답 헤더 정의
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  // 신규 유저 생성 요청
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserRequestDto userRequestDTO,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    BinaryContentDto profileRequest = FileConverter.resolveFileRequest(profile);

    User createdUser = userService.create(userRequestDTO, profileRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping
  public ResponseEntity<List<UserResponseDto>> findAll() {
    List<UserResponseDto> allUsers = userService.findAll();

    return ResponseEntity.status(HttpStatus.OK).body(allUsers);
  }

  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponseDto> update(@PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateDto userUpdateDTO,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    BinaryContentDto profileImg = FileConverter.resolveFileRequest(profile);

    UserResponseDto updatedUser = userService.update(userId, userUpdateDTO, profileImg);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @DeleteMapping(path = "/{userId}")
  public ResponseEntity<String> deleteById(@PathVariable UUID userId) {
    userService.deleteById(userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping(path = "/{userId}/userStatus")
  public ResponseEntity<UserStatusResponseDto> updateUserStatus(@PathVariable UUID userId,
      @RequestBody UserStatusUpdateDto userStatusUpdateDTO) {
    UserStatusResponseDto userStatusResponseDTO = userStatusService.updateByUserId(userId,
        userStatusUpdateDTO);

    return ResponseEntity.status(HttpStatus.OK).body(userStatusResponseDTO);
  }
}
