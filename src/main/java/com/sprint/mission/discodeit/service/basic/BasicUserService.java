package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentData;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreateEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.UserOnlineService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import com.sprint.mission.discodeit.service.command.UpdateUserCommand;
import com.sprint.mission.discodeit.service.command.UpdateUserRoleCommand;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserOnlineService userOnlineService;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResponse create(CreateUserCommand command) {
    validateUserEmail(command.email());
    validateUserName(command.username());

    String encodedPassword = passwordEncoder.encode(command.password());
    User newUser = User.create(
        command.email(),
        command.username(),
        encodedPassword,
        null
    );
    User savedUser = userRepository.save(newUser);

    BinaryContent savedProfile = null;
    if (command.profile() != null) {
      savedProfile = saveProfileImage(command.profile());
    }

    if (savedProfile != null) {
      savedUser.updateProfile(savedProfile);
      userRepository.save(savedUser);
    }

    return toUserResponse(savedUser);
  }

  private void validateUserEmail(String email) {
    userRepository.findByEmail(email).ifPresent(user -> {
      throw new DuplicateEmailException();
    });
  }

  private void validateUserName(String name) {
    userRepository.findByUsername(name).ifPresent(user -> {
      throw new DuplicateNameException();
    });
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse findById(UUID userId) {
    return userRepository.findById(userId).map(this::toUserResponse)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse findByName(String name) {
    return userRepository.findByUsername(name).map(this::toUserResponse)
        .orElseThrow(UserNotFoundException::new);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse findByEmail(String email) {
    return userRepository.findByEmail(email).map(this::toUserResponse)
        .orElseThrow(UserNotFoundException::new);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream().map(this::toUserResponse).toList();
  }

  @Override
  @PreAuthorize("@userSecurity.isSelf(#command.userId)")
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
            String encoded = passwordEncoder.encode(command.newPassword());
            user.updatePassword(encoded);
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
        }).orElseThrow(() -> new UserNotFoundException(command.userId().toString()));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse updateRole(UpdateUserRoleCommand command) {
    return userRepository.findById(command.userId())
        .map(user -> {
          var oldRole = user.getRole();
          if (!oldRole.equals(command.newRole())) {
            user.updateRole(command.newRole());
            User savedUser = userRepository.save(user);
            jwtRegistry.invalidateJwtInformationByUserId(savedUser.getId());
            applicationEventPublisher.publishEvent(
                new RoleUpdatedEvent(savedUser, oldRole,
                    command.newRole()));
            return toUserResponse(savedUser);
          } else {
            // 권한이 변경되지 않은 경우 기존 응답 반환
            return toUserResponse(user);
          }
        }).orElseThrow(() -> new UserNotFoundException(command.userId().toString()));
  }

  @Override
  @PreAuthorize("@userSecurity.isSelf(#userId)")
  public void delete(UUID userId) {
    userRepository.findById(userId).ifPresentOrElse(user -> {
      userRepository.deleteById(userId);

      Optional.ofNullable(user.getProfile())
          .ifPresent(profile -> binaryContentRepository.deleteById(profile.getId()));

      jwtRegistry.invalidateJwtInformationByUserId(userId);
    }, () -> {
      throw new UserNotFoundException(userId.toString());
    });
  }

  private BinaryContent saveProfileImage(BinaryContentData profile) {
    try {
      BinaryContent binaryContent = BinaryContent.create(
          profile.fileName(),
          (long) profile.bytes().length,
          profile.contentType());

      BinaryContent saved = binaryContentRepository.save(binaryContent);

      applicationEventPublisher.publishEvent(
          new BinaryContentCreateEvent(saved.getId(), profile.bytes()));

      return saved;
    } catch (Exception e) {
      log.warn("프로필 이미지 등록 실패: 기본 이미지 사용", e);
      return null;
    }
  }

  private UserResponse toUserResponse(User user) {
    UserResponse base = userMapper.toResponse(user);
    return new UserResponse(
        base.id(),
        base.username(),
        base.email(),
        base.profile(),
        isUserOnline(user.getId()),
        base.role());
  }

  public boolean isUserOnline(UUID userId) {
    return userOnlineService.isOnline(userId);
  }
}