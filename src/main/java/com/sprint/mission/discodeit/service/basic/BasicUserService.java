package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.mapper.EntityDtoMapper;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final EntityDtoMapper entityDtoMapper;

  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw new CustomException.DuplicateUserException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new CustomException.DuplicateUserException("User with username " + username + " already exists");
    }

    BinaryContent profile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          // 1. 메타정보만으로 BinaryContent 생성 및 저장
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
          BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

          // 2. 실제 바이너리 데이터는 Storage에 저장
          binaryContentStorage.put(savedBinaryContent.getId(), bytes);

          return savedBinaryContent;
        })
        .orElse(null);

    String password = userCreateRequest.password();
    User user = new User(username, email, password, profile);

    User savedUser = userRepository.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(savedUser, now);
    userStatusRepository.save(userStatus);

    return entityDtoMapper.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(entityDtoMapper::toDto)
        .orElseThrow(() -> new CustomException.UserNotFoundException("User with id " + userId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return entityDtoMapper.toUserDtoList(userRepository.findAll());
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("User with id " + userId + " not found"));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
      throw new CustomException.DuplicateUserException("User with email " + newEmail + " already exists");
    }
    if (newUsername != null && !newUsername.equals(user.getUsername())
        && userRepository.existsByUsername(newUsername)) {
      throw new CustomException.DuplicateUserException("User with username " + newUsername + " already exists");
    }

    BinaryContent newProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          // 1. 메타정보만으로 BinaryContent 생성 및 저장
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
          BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

          // 2. 실제 바이너리 데이터는 Storage에 저장
          binaryContentStorage.put(savedBinaryContent.getId(), bytes);

          return savedBinaryContent;
        })
        .orElse(user.getProfile());

    String newPassword = userUpdateRequest.newPassword();

    user.update(newUsername, newEmail, newPassword, newProfile);

    return entityDtoMapper.toDto(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("User with id " + userId + " not found"));

    userRepository.delete(user);
  }
}
