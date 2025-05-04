package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.common.exception.UserException;
import com.sprint.mission.discodeit.dto.data.UserResponse;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private static final Logger log = LogManager.getLogger(UserServiceImpl.class);
  //  private static final Logger log = LogManager.getLogger(UserServiceImpl.class);
  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public User create(String email, String name, String password) {
    validateUserEmail(email);
    validateUserName(name);
    User newUser = User.create(email, name, password);
    User user = userRepository.save(newUser);
    userStatusRepository.save(UserStatus.create(user.getId()));
    return user;
  }

  @Override
  public UserResponse create(UserCreateRequest request) {
    validateUserEmail(request.email());
    validateUserName(request.name());

    User newUser = User.create(
        request.email(),
        request.name(),
        request.password(),
        null // profileImageId는 나중에 세팅
    );
    User savedUser = userRepository.save(newUser);

    userStatusRepository.save(UserStatus.create(savedUser.getId()));

    try {
      UUID profileImageId = null;

      if (request.profileImage() != null && request.profileImage().getId() != null) {
        binaryContentRepository.save(request.profileImage());
        profileImageId = request.profileImage().getId();

        savedUser.updateProfileImageId(profileImageId);
        userRepository.save(savedUser);
      }
    } catch (Exception e) {
      // 로깅하고 무시 (기본 null)
//      log.warn("프로필 이미지 등록 실패: 기본 이미지 사용", e);
    }

    return toUserResponse(savedUser);
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
  public Optional<UserResponse> findById(UUID id) {
    return userRepository.findById(id).map(this::toUserResponse);
  }

  @Override
  public Optional<UserResponse> findByName(String name) {
    return userRepository.findByName(name).map(this::toUserResponse);
  }

  @Override
  public Optional<UserResponse> findByEmail(String email) {
    return userRepository.findByEmail(email).map(this::toUserResponse);
  }

  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream().map(this::toUserResponse).toList();
  }

  @Override
  public Optional<UserResponse> update(UserUpdateRequest request) {
    return userRepository.findById(request.id())
        .map(user -> {
          if (request.name() != null && !request.name().equals(user.getName())) {
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
  public Optional<UserResponse> delete(UUID id) {
    return userRepository.findById(id).map(user -> {
      userRepository.delete(id);

      Optional.ofNullable(user.getProfileImageId())
          .ifPresent(binaryContentRepository::delete);

      userStatusRepository.delete(id);
      return toUserResponse(user);
    });
  }

  private UserResponse toUserResponse(User user) {
    Optional<UserStatus> userStatus = userStatusRepository.findByUserId(user.getId());
    return UserResponse.from(user, userStatus);
  }
}