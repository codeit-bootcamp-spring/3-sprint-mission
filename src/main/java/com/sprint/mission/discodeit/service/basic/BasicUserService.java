package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.UserException;
import com.sprint.mission.discodeit.dto.data.UserResponse;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
  public UserResponse createUser(UserCreateRequest request) {
    validateUserEmail(request.email());
    validateUserName(request.name());

    UUID profileImageId = null;
    if (request.profileImage() != null && request.profileImage().getId() != null) {
      binaryContentRepository.save(request.profileImage());
      profileImageId = request.profileImage().getId();
    }

    User newUser = User.create(request.email(), request.name(), request.password(), profileImageId);
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
  public Optional<UserResponse> updateUser(UserUpdateRequest request) {
    return userRepository.findById(request.id())
        .map(user -> {
          if (request.name() != null) {
            validateUserName(request.name());
            user.updateName(request.name());
          }
          if (request.password() != null) {
            user.updatePassword(request.password());
          }
          if (request.profileImageId() != null) {
            user.updateProfileImageId(request.profileImageId());
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
    return UserResponse.from(user, userStatus);
  }
}