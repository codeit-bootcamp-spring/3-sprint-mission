package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// Lombok( 생성자 대체 )
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  // 리펙토링

  @Override
  public User create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.getUserName();
    String email = userCreateRequest.getEmail();

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }

    UUID nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.getFileName();
          String contentType = profileRequest.getContentType();
          byte[] bytes = profileRequest.getBytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType, bytes);
          return binaryContentRepository.save(binaryContent).getId();
        })
        .orElse(null);
    String password = userCreateRequest.getPassword();

    User user = new User(username, email, password, nullableProfileId);
    User createdUser = userRepository.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser.getUserId(), now);
    userStatusRepository.save(userStatus);

    return createdUser;
  }

  @Override
  public UserDTO find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    // 온라인 여부
    boolean isOnline = userStatusRepository.findById(user.getUserId())
        .map(UserStatus::isOnline)
        .orElse(false);

    // 조회 정보 반환
    return new UserDTO(
        user.getUserId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUserName(),
        user.getEmail(),
        user.getProfileId(),
        isOnline
    );
  }

  @Override
  public List<UserDTO> findAll() {
    // 리스트로 전체 유저를 조회
    List<User> users = userRepository.findAll();
    return users.stream()
        // 온라인 여부 판별 후 해당 정보 값을 dto로 반환
        .map(user -> {
          boolean isOnline = userStatusRepository.findById(user.getUserId())
              .map(UserStatus::isOnline)
              .orElse(false);

          return new UserDTO(
              user.getUserId(),
              user.getCreatedAt(),
              user.getUpdatedAt(),
              user.getUserName(),
              user.getEmail(),
              user.getProfileId(),
              isOnline
          );
        })
        // List로 저장
        .collect(Collectors.toList());
  }

  @Override
  public User update(UUID id, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
    // 조회 우선
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    // 선택적 프로필 이미지 개선
    binaryContentCreateRequest.ifPresent(request -> {
      BinaryContent profileImage = new BinaryContent(
          request.getFileName(),
          (long) request.getBytes().length,
          request.getContentType(),
          request.getBytes()
      );
      // BinaryContent 저장 ( >> 업데이트 )
      binaryContentRepository.save(profileImage);
    });

    // 사용자 정보 업데이트 ( 비밀번호 별도 )
    user.update(
        userUpdateRequest.getNewUsername(),
        userUpdateRequest.getNewEmail(),
        userUpdateRequest.getNewPassword()
    );
    return userRepository.save(user);
  }


  @Override
  public void delete(UUID userId) {

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " not found");
    }

    // cascade 옵션 적용 ( BinaryContent && UserStatus )
    binaryContentRepository.deleteById(userId);
    userStatusRepository.deleteById(userId);
    userRepository.deleteById(userId);
  }
}
