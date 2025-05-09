package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_BOTH;
import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_EMAIL;
import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_USERNAME;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.global.exception.DuplicateUserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public UserResponse create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    validateDuplicate(request.username(), request.email());

    UUID profileId = optionalProfileCreateRequest
        .map(profile -> {
          BinaryContent binaryContent = new BinaryContent(
              profile.filename(),
              (long) profile.data().length,
              profile.contentType(),
              profile.data()
          );
          return binaryContentRepository.save(binaryContent).getId();
        })
        .orElse(null);

    User user = new User(request.username(), request.email(), request.password(), profileId);
    userRepository.save(user);

    userStatusRepository.save(new UserStatus(user.getId(), Instant.now()));

    return toResponse(user, true);
  }

  @Override
  public UserDTO find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    return toDto(user);
  }

  @Override
  public List<UserDTO> findAll() {
    return userRepository.findAll().stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public UserResponse update(UUID userId, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    validateDuplicate(request.NewUsername(), request.NewEmail());

    UUID newProfileId = optionalProfileCreateRequest
        .map(profile -> {
          Optional.ofNullable(user.getProfileId())
              .ifPresent(binaryContentRepository::deleteById);
          BinaryContent binaryContent = new BinaryContent(
              profile.filename(),
              (long) profile.data().length,
              profile.contentType(),
              profile.data()
          );
          return binaryContentRepository.save(binaryContent).getId();
        })
        .orElse(user.getProfileId());

    user.update(request.NewUsername(), request.NewEmail(), request.NewPassword(), newProfileId);
    userRepository.save(user);

    return toResponse(user, isOnline(user.getId()));
  }

  @Override
  public UserResponse delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    Optional.ofNullable(user.getProfileId())
        .ifPresent(binaryContentRepository::deleteById);
    userStatusRepository.deleteByUserId(userId);
    userRepository.deleteById(userId);

    return toResponse(user, false);
  }

  private void validateDuplicate(String username, String email) {
    boolean usernameExists = userRepository.existsByUsername(username);
    boolean emailExists = userRepository.existsByEmail(email);

    if (usernameExists && emailExists) {
      throw new DuplicateUserException(DUPLICATE_BOTH);
    } else if (usernameExists) {
      throw new DuplicateUserException(DUPLICATE_USERNAME);
    } else if (emailExists) {
      throw new DuplicateUserException(DUPLICATE_EMAIL);
    }
  }

  private boolean isOnline(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .map(UserStatus::isOnline)
        .orElse(false);
  }

  private UserResponse toResponse(User user, boolean isOnline) {
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        isOnline
    );
  }

  private UserDTO toDto(User user) {
    Boolean online = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline)
        .orElse(null);

    return new UserDTO(
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
