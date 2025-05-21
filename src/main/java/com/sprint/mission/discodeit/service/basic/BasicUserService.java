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
  public UserResponse create(UserCreateRequest request, BinaryContentCreateRequest profile) {
    validateUserEmail(request.email());
    validateUserName(request.username());

    User newUser = User.create(
        request.email(),
        request.username(),
        request.password(),
        null
    );
    User savedUser = userRepository.save(newUser);

    userStatusRepository.save(UserStatus.create(savedUser.getId()));

    try {
      UUID profileImageId = null;

      if (profile != null) {
        String fileName = profile.fileName();
        String contentType = profile.contentType();
        byte[] bytes = profile.bytes();

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
  public UserResponse findById(UUID userId) {
    return userRepository.findById(userId).map(this::toUserResponse)
        .orElseThrow(() -> UserException.notFound(userId));
  }

  @Override
  public UserResponse findByName(String name) {
    return userRepository.findByName(name).map(this::toUserResponse)
        .orElseThrow(UserException::notFound);
  }

  @Override
  public UserResponse findByEmail(String email) {
    return userRepository.findByEmail(email).map(this::toUserResponse)
        .orElseThrow(UserException::notFound);
  }

  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream().map(this::toUserResponse).toList();
  }

  @Override
  public UserResponse update(UUID userId, UserUpdateRequest request,
      BinaryContentCreateRequest profile) {
    return userRepository.findById(userId)
        .map(user -> {
          if (request.newName() != null && !request.newName().equals(user.getName())) {
            validateUserName(request.newName());
            user.updateName(request.newName());
          }
          if (request.newPassword() != null) {
            user.updatePassword(request.newPassword());
          }
          try {
            UUID profileImageId = null;

            if (profile != null) {
              String fileName = profile.fileName();
              String contentType = profile.contentType();
              byte[] bytes = profile.bytes();

              BinaryContent binaryContent = BinaryContent.create(fileName, (long) fileName.length(),
                  contentType, bytes);
              profileImageId = binaryContentRepository.save(binaryContent).getId();

              user.updateProfileId(profileImageId);
              userRepository.save(user);
            }
          } catch (Exception e) {
            log.warn("프로필 이미지 등록 실패: 기본 이미지 사용", e);
          }
          User savedUser = userRepository.save(user);
          return toUserResponse(savedUser);
        }).orElseThrow(() -> UserException.notFound(userId));
  }

  @Override
  public UserResponse delete(UUID userId) {
    return userRepository.findById(userId).map(user -> {
      userRepository.delete(userId);

      Optional.ofNullable(user.getProfileId())
          .ifPresent(binaryContentRepository::delete);

      userStatusRepository.findByUserId(userId).ifPresent(status -> {
        userStatusRepository.delete(status.getId());
      });

      return toUserResponse(user);
    }).orElseThrow(() -> UserException.notFound(userId));
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