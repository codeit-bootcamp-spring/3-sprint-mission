package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private static final Logger log = LogManager.getLogger(BasicUserService.class);

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public UserResponse create(UserCreateRequest request, BinaryContentCreateRequest profileImage) {
    validateUserEmail(request.email());
    validateUserName(request.name());

    User newUser = User.create(
        request.email(),
        request.name(),
        request.password(),
        null
    );
    User savedUser = userRepository.save(newUser);

    userStatusRepository.save(UserStatus.create(savedUser.getId()));

    try {
      UUID profileImageId = null;

      if (profileImage != null) {
        String fileName = profileImage.fileName();
        String contentType = profileImage.contentType();
        byte[] bytes = profileImage.bytes();

        BinaryContent binaryContent = BinaryContent.create(fileName, (long) fileName.length(),
            contentType, bytes);
        profileImageId = binaryContentRepository.save(binaryContent).getId();

        savedUser.updateProfileId(profileImageId);
        userRepository.save(savedUser);
      }
    } catch (Exception e) {
      log.warn("프로필 이미지 등록 실패: 기본 이미지 사용", e);
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
  public Optional<UserResponse> update(UUID userId, UserUpdateRequest request) {
    return userRepository.findById(userId)
        .map(user -> {
          if (request.name() != null && !request.name().equals(user.getName())) {
            validateUserName(request.name());
            user.updateName(request.name());
          }
          if (request.password() != null) {
            user.updatePassword(request.password());
          }
          if (request.profileImageId() != null) {
            user.updateProfileId(request.profileImageId());
          }
          User savedUser = userRepository.save(user);
          return toUserResponse(savedUser);
        });
  }

  @Override
  public Optional<UserResponse> delete(UUID id) {
    return userRepository.findById(id).map(user -> {
      userRepository.delete(id);

      Optional.ofNullable(user.getProfileId())
          .ifPresent(binaryContentRepository::delete);

      userStatusRepository.findByUserId(id).ifPresent(status -> {
        userStatusRepository.delete(status.getId());
      });

      return toUserResponse(user);
    });
  }

  private UserResponse toUserResponse(User user) {
    Boolean isOnline = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline).orElse(null);
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getName(),
        user.getEmail(),
        user.getProfileId(),
        Boolean.TRUE.equals(isOnline)
    );
  }
}