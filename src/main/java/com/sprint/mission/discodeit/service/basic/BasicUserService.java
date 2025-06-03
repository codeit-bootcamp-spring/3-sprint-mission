package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import com.sprint.mission.discodeit.service.command.UpdateUserCommand;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.vo.BinaryContentData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private static final Logger log = LogManager.getLogger(BasicUserService.class);

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;

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
    userStatusRepository.save(UserStatus.create(savedUser));

    BinaryContent savedProfile = null;
    if (command.profile() != null) {
      // 프로필 이미지 첨부 시 저장 및 유저 업데이트
      savedProfile = saveProfileImage(command.profile());
    }

    if (savedProfile != null) {
      savedUser.updateProfile(savedProfile);
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
    userRepository.findByUsername(name).ifPresent(user -> {
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
    return userRepository.findByUsername(name).map(this::toUserResponse)
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
          if (command.newName() != null && !command.newName().equals(user.getUsername())) {
            validateUserName(command.newName());
            user.updateName(command.newName());
          }
          if (command.newEmail() != null && !command.newEmail().equals(user.getEmail())) {
            validateUserEmail(command.newEmail());
            user.updateEmail(command.newEmail());
          }
          if (command.newPassword() != null) {
            user.updatePassword(command.newPassword());
          }

          BinaryContent savedProfile = null;
          if (command.profile() != null && command.profile().bytes() != null) {
            Optional.ofNullable(user.getProfile())
                .ifPresent(profile -> binaryContentRepository.deleteById(profile.getId()));

            savedProfile = saveProfileImage(command.profile());
            user.updateProfile(savedProfile);
          }

          User savedUser = userRepository.save(user);
          return toUserResponse(savedUser);
        }).orElseThrow(() -> UserException.notFound(command.userId()));
  }

  @Override
  public void delete(UUID userId) {
    userRepository.findById(userId).ifPresentOrElse(user -> {
      userRepository.deleteById(userId);

      Optional.ofNullable(user.getProfile())
          .ifPresent(profile -> binaryContentRepository.deleteById(profile.getId()));

      userStatusRepository.findByUserId(userId)
          .ifPresent(status -> userStatusRepository.deleteById(status.getId()));
    }, () -> {
      throw UserException.notFound(userId);
    });
  }

  private BinaryContent saveProfileImage(BinaryContentData profile) {
    try {
      BinaryContent binaryContent = BinaryContent.create(
          profile.fileName(),
          (long) profile.bytes().length,
          profile.contentType()
      );

      BinaryContent saved = binaryContentRepository.save(binaryContent);

      binaryContentStorage.put(saved.getId(), profile.bytes());

      return saved;
    } catch (Exception e) {
      log.warn("프로필 이미지 등록 실패: 기본 이미지 사용", e);
      return null;
    }
  }

  private UserResponse toUserResponse(User user) {
    boolean isOnline = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline)
        .orElse(false);

    UserResponse base = userMapper.toResponse(user);
    return new UserResponse(
        base.id(),
        base.username(),
        base.email(),
        base.profile(),
        isOnline
    );
  }
}