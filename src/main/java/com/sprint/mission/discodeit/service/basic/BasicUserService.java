package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.UserException;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public User createUser(String email, String name, String password) {
    validateUserEmail(email);
    validateUserName(name);
    User newUser = User.create(email, name, password);
    User user = userRepository.save(newUser);
    userStatusRepository.save(UserStatus.create(user.getId()));
    return user;
  }

  @Override
  public UserResponse createUser(UserCreateRequest ucr) {
    validateUserEmail(ucr.email());
    validateUserName(ucr.name());

    UUID profileImageId = null;
    if (ucr.profileImage() != null && ucr.profileImage().getId() != null) {
      profileImageId = ucr.profileImage().getId();
    }

    User newUser = User.create(ucr.email(), ucr.name(), ucr.password(), profileImageId);
    User user = userRepository.save(newUser);
    userStatusRepository.save(UserStatus.create(user.getId()));
    return toUserResponse(user);
  }

  private void validateUserEmail(String email) {
    userRepository.findByEmail(email).ifPresent(user -> {
      throw UserException.duplicateEmail();
    });
  }

  private void validateUserName(String name) {
    userRepository.findByName(name).ifPresent(user -> {
      throw UserException.duplicateName();
    });
  }

  @Override
  public Optional<UserResponse> getUserById(UUID id) {
    return userRepository.findById(id).map(this::toUserResponse);
  }

  @Override
  public Optional<UserResponse> getUserByName(String name) {
    return userRepository.findByName(name).map(this::toUserResponse);
  }

  @Override
  public Optional<UserResponse> getUserByEmail(String email) {
    return userRepository.findByEmail(email).map(this::toUserResponse);
  }

  @Override
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(this::toUserResponse).toList();
  }

  @Override
  public Optional<UserResponse> updateUser(UserUpdateRequest urr) {
    return userRepository.findById(urr.id())
        .map(user -> {
          if (urr.name() != null) {
            validateUserName(urr.name());
            user.updateName(urr.name());
          }
          if (urr.password() != null) {
            user.updatePassword(urr.password());
          }
          if (urr.profileImageId() != null) {
            user.updateProfileImageId(urr.profileImageId());
          }
          User savedUser = userRepository.save(user);
          return toUserResponse(savedUser);
        });
  }

  @Override
  public Optional<UserResponse> deleteUser(UUID id) {
    return userRepository.findById(id).map(user -> {
      userRepository.deleteById(id);

      Optional.ofNullable(user.getProfileImageId())
          .ifPresent(binaryContentRepository::deleteById);
      
      userStatusRepository.deleteById(id);
      return toUserResponse(user);
    });
  }

  private UserResponse toUserResponse(User user) {
    Optional<UserStatus> userStatus = userStatusRepository.findById(user.getId());
    boolean isActive = userStatus.map(UserStatus::isCurrentlyActive).orElse(false);
    return new UserResponse(user.getId(), user.getEmail(), user.getName(),
        isActive, user.getProfileImageId());
  }
}