package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import com.sprint.mission.discodeit.service.command.UpdateUserCommand;
import com.sprint.mission.discodeit.vo.BinaryContentData;
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
  public UserResponse create(CreateUserCommand command) {
    validateUserEmail(command.email());
    validateUserName(command.username());

    // 유저 생성
    User newUser = User.create(
        command.email(),
        command.username(),
        command.password(),
        null // 일단 profileId 없음
    );
    User savedUser = userRepository.save(newUser);

    // 유저 상태 초기화
    userStatusRepository.save(UserStatus.create(savedUser.getId()));

    // 프로필 이미지 첨부 시 저장 및 유저 업데이트
    UUID savedProfileImageId = saveProfileImage(command.profile());

    if (savedProfileImageId != null) {
      savedUser.updateProfileId(savedProfileImageId);
      userRepository.save(savedUser); // 프로필 반영 후 다시 저장
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
  public UserResponse update(UpdateUserCommand command) {
    return userRepository.findById(command.userId())
        .map(user -> {
          if (command.newName() != null && !command.newName().equals(user.getName())) {
            validateUserName(command.newName());
            user.updateName(command.newName());
          }
          if (command.newPassword() != null) {
            user.updatePassword(command.newPassword());
          }

          // 프로필 이미지 첨부 시 저장 및 유저 업데이트
          UUID savedProfileImageId = saveProfileImage(command.profile());

          user.updateProfileId(savedProfileImageId);
          User savedUser = userRepository.save(user);

          return toUserResponse(savedUser);
        }).orElseThrow(() -> UserException.notFound(command.userId()));
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

  private UUID saveProfileImage(BinaryContentData profile) {
    try {
      if (profile.bytes() != null) {
        BinaryContent binaryContent = BinaryContent.create(
            profile.fileName(),
            (long) profile.fileName().length(), // 길이를 size로 사용하는 경우
            profile.contentType(),
            profile.bytes()
        );

        return binaryContentRepository.save(binaryContent).getId();
      }
      return null;
    } catch (Exception e) {
      log.warn("프로필 이미지 등록 실패: 기본 이미지 사용", e);
      return null;
    }
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