package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;

  @Override
  public User create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw new CustomException.DuplicateUserException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new CustomException.DuplicateUserException("User with username " + username + " already exists");
    }

    BinaryContent profile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          return new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
        })
        .orElse(null);

    String password = userCreateRequest.password();
    User user = new User(username, email, password, profile);

    User savedUser = userRepository.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(savedUser, now);

    return savedUser;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(this::toDto)
        .orElseThrow(() -> new CustomException.UserNotFoundException("User with id " + userId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public User update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("User with id " + userId + " not found"));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
      throw new CustomException.DuplicateUserException("User with email " + newEmail + " already exists");
    }
    if (newUsername != null && !newUsername.equals(user.getUsername())
        && userRepository.existsByUsername(newUsername)) {
      throw new CustomException.DuplicateUserException("User with username " + newUsername + " already exists");
    }

    BinaryContent newProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          return new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
        })
        .orElse(user.getProfile());

    String newPassword = userUpdateRequest.newPassword();

    user.update(newUsername, newEmail, newPassword, newProfile);

    return user;
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("User with id " + userId + " not found"));

    userRepository.delete(user);
  }

  private UserDto toDto(User user) {
    Boolean online = Optional.ofNullable(user.getUserStatus())
        .map(UserStatus::isOnline)
        .orElse(null);

    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        Optional.ofNullable(user.getProfile()).map(BinaryContent::getId).orElse(null),
        online);
  }
}
