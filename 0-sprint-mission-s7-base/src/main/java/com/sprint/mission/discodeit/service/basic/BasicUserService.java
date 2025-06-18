package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      log.error("[유저 등록 실패] 해당 email은 이미 등록되어 있습니다. email : {}", email);
      throw new IllegalArgumentException();
    }
    if (userRepository.existsByUsername(username)) {
      log.error("[유저 등록 실패] 해당 유저 이름은 이미 등록되어 있습니다. username : {}", username);
      throw new IllegalArgumentException();
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);
    log.info("[유저 등록] 유저 프로필이 생성되었습니다.");
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    log.info("[유저 등록] 유저 ID: {}", user.getId());

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);
    log.info("[유저 상태 등록] 유저 상태 ID : {}", userStatus.getId());

    userRepository.save(user);
    log.info("[유저 등록 성공] 유저 상태 ID : {}", userStatus.getId());
    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
     log.info("[유저 조회 시도] 유저 ID : {}", userId);

    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> {
            log.error("[유저 조회 실패] 해당 유저를 찾을 수 없습니다. 유저 ID : {}", userId);
        return new NoSuchElementException();
        });
  }

  @Override
  public List<UserDto> findAll() {
      log.info("[모든 유저 조회 시도]");

    return userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
      log.info("[유저 수정 시도]");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);
      log.info("[유저 수정] 유저 프로필이 수정되었습니다. 유저 ID : {}", userId);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);
      log.info("[유저 수정 성공] 유저 ID : {}", userId);

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
      log.info("[유저 삭제 시도] 유저 ID : {}", userId);

    if (userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " not found");
    }

    userRepository.deleteById(userId);
      log.info("[유저 삭제 성공] 유저 ID: {}", userId);
  }
}
