package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.UserDto;
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
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  public User create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException(ErrorMessages.format("Email", ErrorMessages.ERROR_EXISTS));
    }
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException(
          ErrorMessages.format("UserName", ErrorMessages.ERROR_EXISTS));
    }

    UUID nullableProfileId = optionalProfileCreateRequest
        .map(profileCreateRequest -> {
          String fileName = profileCreateRequest.fileName();
          String contentType = profileCreateRequest.contentType();
          byte[] bytes = profileCreateRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType, bytes);
          return binaryContentRepository.save(binaryContent).getId();
        })
        .orElse(null);

    User user = new User(request.username(), request.email(), request.password(),
        nullableProfileId);
    User savedUser = userRepository.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(savedUser.getId(), now);
    userStatusRepository.save(userStatus);

    return savedUser;
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository.find(userId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("User", ErrorMessages.ERROR_NOT_FOUND))
        );
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public User update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    //1. 수정할 엔티티 조회
    User user = userRepository.find(userId).orElseThrow(() -> new NoSuchElementException(
        ErrorMessages.format("User", ErrorMessages.ERROR_NOT_FOUND))
    );

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    //2. username/email 중복 체크
    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException(ErrorMessages.format("Email", ErrorMessages.ERROR_EXISTS));
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException(
          ErrorMessages.format("UserName", ErrorMessages.ERROR_EXISTS));
    }

    UUID nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          Optional.ofNullable(user.getProfileId())
              .ifPresent(binaryContentRepository::deleteById);

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType, bytes);
          return binaryContentRepository.save(binaryContent).getId();
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfileId);

    return userRepository.save(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.find(userId).orElseThrow(() -> new NoSuchElementException(
        ErrorMessages.format("Channel", ErrorMessages.ERROR_NOT_FOUND))
    );
    Optional.ofNullable(user.getProfileId())
        .ifPresent(binaryContentRepository::deleteById);
    userStatusRepository.deleteByUserId(userId);

    userRepository.deleteById(userId);
  }

  private UserDto toDto(User user) {
    Boolean online = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline)
        .orElse(null);

    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        online
    );
  }
}
