package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserResponse create(UserRequest request,
      Optional<BinaryContentCreateRequest> profileCreateRequest) {
    String username = request.username();
    String email = request.email();

    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("User with email already exists");
    }
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("User with username already exists");
    }

    BinaryContent nullableProfile = profileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          BinaryContent save = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(save.getId(), bytes);
          return save;
        })
        .orElse(null);
    String password = request.password();
    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);
    userStatusRepository.save(userStatus);

    User createdUser = userRepository.save(user);

    return userMapper.entityToDto(createdUser);
  }

  @Transactional(readOnly = true)
  @Override
  public UserResponse find(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::entityToDto)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::entityToDto)
        .toList();
  }

  @Override
  @Transactional
  public UserResponse update(UUID id, UserRequest.Update request,
      Optional<BinaryContentCreateRequest> profileCreateRequest) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    String newUsername = request.newUsername();
    String newEmail = request.newEmail();

    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(
        newEmail)) {
      throw new IllegalArgumentException("User with email already exists");
    }
    if (newUsername != null && !newUsername.equals(user.getUsername())
        && userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username already exists");
    }

    BinaryContent nullableProfile = profileCreateRequest
        .map(profileRequest -> {
          Optional.ofNullable(user.getProfile())
              .map(BinaryContent::getId)
              .ifPresent(binaryContentRepository::deleteById);

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          BinaryContent save = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(save.getId(), bytes);
          return save;

        })
        .orElse(null);

    String newPassword = request.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);

    return userMapper.entityToDto(user);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    userStatusRepository.deleteByUserId(id);

    userRepository.deleteById(id);
  }


}
