package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  //
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto create(
      @Valid UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
      log.error("사용자 생성 실패 - username={}, email={}", username, email);
      throw new DuplicateUserException(username, email);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
          log.debug("binaryContent entity 생성: {}", binaryContent);
          binaryContentRepository.save(binaryContent);

          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    User createdUser = userRepository.save(user);
    log.debug("사용자 entity 생성: {}",  createdUser);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser, now);
    log.info("사용자 읽음 상태 entity 생성: {}", userStatus);
    userStatusRepository.save(userStatus);

    return userMapper.toDto(createdUser);
  }

  @Override
  public UserDto find(@NotNull UUID userId) {
    return userRepository.findById(userId)
        .map(this::toDto)
        .orElseThrow(() -> {
          log.error("사용자 조회 실패 - userId={}", userId);
          return new UserNotFoundException(userId);
        });
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(@NotNull UUID userId, @Valid UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자 조회 실패 - userId={}", userId);
          return new UserNotFoundException(userId);
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByUsername(newUsername) || userRepository.existsByEmail(newEmail)) {
      throw new DuplicateUserException(newUsername, newEmail);
    }

    BinaryContent newProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          Optional.ofNullable(user.getProfile())
              .ifPresent(binaryContentRepository::delete);

          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          log.debug("파일 entity 생성: {}",   binaryContent);

          BinaryContent saveBc = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return saveBc;
        })
        .orElse(user.getProfile());

    user.update(
        userUpdateRequest.newUsername(),
        userUpdateRequest.newEmail(),
        userUpdateRequest.newPassword(),
        newProfile
    );
    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public void delete(@NotNull UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자 조회 실패 - userId={}", userId);
          return new UserNotFoundException(userId);
        });

    Optional.ofNullable(user.getProfile())
        .ifPresent(binaryContentRepository::delete);

    userStatusRepository.deleteByUserId(userId);
    userRepository.delete(user);
  }

  private UserDto toDto(User user) {
    Boolean online = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline)
        .orElse(null);

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() != null ? binaryContentMapper.toDto(user.getProfile()) : null,
        online
    );
  }
}
