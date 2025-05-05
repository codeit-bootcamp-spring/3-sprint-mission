package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
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
  public UserResponse create(UserCreateRequest request) {
    validateDuplicate(request.getUsername(), request.getEmail());

    User user = new User(
        request.getUsername(),
        request.getEmail(),
        request.getPassword()
    );
    userRepository.save(user);

    UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
    userStatusRepository.save(userStatus);

    return toResponse(user, true);
  }

  @Override
  public User create(String username) {
    return null;
  }

  @Override
  public UserResponse findById(UUID id) {
    User user = userRepository.findById(id);
    if (user == null) {
      return null;
    }

    UserStatus status = userStatusRepository.findByUserId(id);
    boolean online = status != null && status.isOnline();

    return toResponse(user, online);
  }

  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream()
        .map(user -> {
          UserStatus status = userStatusRepository.findByUserId(user.getId());
          boolean online = status != null && status.isOnline();
          return toResponse(user, online);
        })
        .collect(Collectors.toList());
  }

  @Override
  public User update(UUID id, String newUsername) {
    return null;
  }

  @Override
  public UserResponse update(UserUpdateRequest request) {
    User user = userRepository.findById(request.getUserId());
    if (user == null) {
      return null;
    }

    user.update(request.getUsername(), request.getEmail());
    userRepository.save(user);

    request.getProfileImage().ifPresent(image -> {
      binaryContentRepository.deleteByUserId(user.getId());
      BinaryContent profile = new BinaryContent(user.getId(), image);
      binaryContentRepository.save(profile);
    });

    return toResponse(user, isOnline(user.getId()));
  }


  @Override
  public UserResponse delete(UUID id) {
    User user = userRepository.findById(id);
    if (user == null) {
      return null;
    }

    userRepository.delete(id);
    userStatusRepository.deleteByUserId(id);
    binaryContentRepository.deleteByUserId(id);

    return toResponse(user, false);
  }

  private void validateDuplicate(String username, String email) {
    boolean usernameExists = userRepository.findByUsername(username) != null;
    boolean emailExists = userRepository.findByEmail(email) != null;

    if (usernameExists && emailExists) {
      throw new IllegalArgumentException("사용자 이름과 이메일 모두 이미 사용 중입니다.");
    } else if (usernameExists) {
      throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
    } else if (emailExists) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }
  }

  private UserResponse toResponse(User user, boolean isOnline) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        isOnline
    );
  }

  private boolean isOnline(UUID userId) {
    UserStatus status = userStatusRepository.findByUserId(userId);
    return status != null && status.isOnline();
  }
}