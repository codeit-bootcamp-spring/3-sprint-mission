package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  //
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();
    String password = userCreateRequest.password();

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }

    BinaryContent profile = null;
    if (optionalProfileCreateRequest.isPresent()) {
      BinaryContentCreateRequest profileCreateRequest = optionalProfileCreateRequest.get();
      profile = BinaryContent.builder()
          .fileName(profileCreateRequest.fileName())
          .size((long) profileCreateRequest.bytes().length)
          .contentType(profileCreateRequest.contentType())
          .build();
    }

    UserStatus userStatus = UserStatus.builder()
        .lastActiveAt(Instant.now())
        .build();

    User user = User.builder()
        .username(username)
        .email(email)
        .password(password)
        .profile(profile)
        .status(userStatus)
        .build();

    userStatus.setUser(user);

    User savedUser = userRepository.save(user);

    if (optionalProfileCreateRequest.isPresent()) {
      binaryContentStorage.put(savedUser.getProfile().getId(),
          optionalProfileCreateRequest.get().bytes());
    }

    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true) // 연관관계가 존재하는 경우 readOnly를 권장
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = userUpdateRequest.newUsername();

    String newEmail = userUpdateRequest.newEmail();

    String newPassword = userUpdateRequest.newPassword();

    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(
        newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (newUsername != null && !newUsername.equals(user.getUsername())
        && userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    BinaryContent newProfile = null;
    if (optionalProfileCreateRequest.isPresent()) {
      BinaryContentCreateRequest profileUpdateRequest = optionalProfileCreateRequest.get();

      newProfile = BinaryContent.builder()
          .fileName(profileUpdateRequest.fileName())
          .size((long) profileUpdateRequest.bytes().length)
          .contentType(profileUpdateRequest.contentType())
          .build();

      BinaryContent savedProfile = binaryContentRepository.save(newProfile);
      binaryContentStorage.put(savedProfile.getId(), profileUpdateRequest.bytes());
      newProfile = savedProfile;
    }

    UserStatus userStatus = user.getStatus();

    user.update(newUsername, newEmail, newPassword, newProfile, userStatus);

    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    if (user.getProfile() != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }

    userRepository.delete(user);
  }

}
