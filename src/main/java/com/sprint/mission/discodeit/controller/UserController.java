package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.user.FriendReqeustDTO;
import com.sprint.mission.discodeit.dto.user.UserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  // 신규 유저 생성 요청
  @PostMapping(path = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(@RequestPart("userRequest") UserRequestDTO userRequestDTO,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    BinaryContentDTO profileRequest = resolveProfileRequest(profile);

    User createdUser = userService.create(userRequestDTO, profileRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping(path = "/users/{userId}")
  public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID userId) {
    UserResponseDTO foundUser = userService.findById(userId);

    return ResponseEntity.status(HttpStatus.OK).body(foundUser);
  }

  @GetMapping(path = "/users/name")
  public ResponseEntity<List<UserResponseDTO>> findByName(@RequestParam String name) {
    List<UserResponseDTO> foundUser = userService.findByNameContaining(name);

    return ResponseEntity.status(HttpStatus.OK).body(foundUser);
  }

  @GetMapping(path = "/users/email")
  public ResponseEntity<UserResponseDTO> findByEmail(@RequestParam String email) {
    UserResponseDTO foundUser = userService.findByEmail(email);

    return ResponseEntity.status(HttpStatus.OK).body(foundUser);
  }

  @GetMapping(path = "/users")
  public ResponseEntity<List<UserResponseDTO>> findAll() {
    List<UserResponseDTO> allUsers = userService.findAll();

    return ResponseEntity.status(HttpStatus.OK).body(allUsers);
  }

  @PatchMapping(path = "/users/{userId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponseDTO> updateProfileImage(@PathVariable UUID userId,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    BinaryContentDTO profileRequest = resolveProfileRequest(profile);

    UserResponseDTO updatedUser = userService.updateProfileImage(userId, profileRequest);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @PatchMapping(path = "/users/{userId}/info")
  public ResponseEntity<UserResponseDTO> updateUserInfo(@PathVariable UUID userId,
      @RequestBody UserRequestDTO userRequestDTO) {
    UserResponseDTO updatedUser = userService.updateUserInfo(userId, userRequestDTO);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @DeleteMapping(path = "/users/{userId}")
  public ResponseEntity<String> deleteById(@PathVariable UUID userId) {
    userService.deleteById(userId);

    return ResponseEntity.status(HttpStatus.OK).body("[Success]: 사용자 삭제 성공!");
  }

  @PatchMapping(path = "/users/{userId}/userStatus")
  public ResponseEntity<UserStatusResponseDTO> updateUserStatus(@PathVariable UUID userId,
      @RequestBody UserStatusUpdateDTO userStatusUpdateDTO) {
    UserStatusResponseDTO userStatusResponseDTO = userStatusService.updateByUserId(userId,
        userStatusUpdateDTO);

    return ResponseEntity.status(HttpStatus.OK).body(userStatusResponseDTO);
  }

  @PostMapping(path = "/users/friends")
  public ResponseEntity<String> addFriend(@RequestBody FriendReqeustDTO friendReqeustDTO) {
    userService.addFriend(friendReqeustDTO);

    return ResponseEntity.status(HttpStatus.OK).body("[Success]: 친구 추가 성공!");
  }

  @DeleteMapping(path = "/users/friends")
  public ResponseEntity<String> deleteFriend(@RequestBody FriendReqeustDTO friendReqeustDTO) {
    userService.deleteFriend(friendReqeustDTO);

    return ResponseEntity.status(HttpStatus.OK).body("[Success]: 친구 삭제 성공!");
  }

  // MultipartFile 타입의 요청 값을 BinaryContentDTO 타입으로 변환하기 위한 메서드
  private BinaryContentDTO resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
      return null;
    } else {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
      try {
        BinaryContentDTO binaryContentDTO = new BinaryContentDTO(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes());

        return binaryContentDTO;
      } catch (IOException e) {
        throw new RuntimeException("파일 '" + profile.getOriginalFilename() + "' 처리 중 오류 발생", e);
      }
    }
  }
}
