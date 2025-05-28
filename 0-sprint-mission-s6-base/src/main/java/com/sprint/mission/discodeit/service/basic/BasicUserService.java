package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.request.UserRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

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
      MultipartFile userProfile) {
    String username = request.username();
    String email = request.email();

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }

    BinaryContent newProFile = null;
    if (userProfile != null && !userProfile.isEmpty()) {
      newProFile = binaryContentRepository.save(BinaryContent.createBinaryContent(
          userProfile.getName(),
          userProfile.getSize(),
          userProfile.getContentType()
      ));
      binaryContentStorage.put(newProFile.getId(), convertToBytes(userProfile));
    }
    User newUser = new User(username, email, request.password(), newProFile);
    User createdUser = userRepository.save(newUser);
    UserStatus newUserStaus = userStatusRepository.save(UserStatus.createUserStatus(newUser));
    newUser.updateUserStatus(newUserStaus);

    log.info("생성된 유저 : {}", newUser);

    return userMapper.entityToDto(createdUser);
  }

  @Override
  public UserResponse find(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::entityToDto)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
  }

  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::entityToDto)
        .toList();
  }

  @Override
  @Transactional
  public UserResponse update(UUID id, UserRequest request,
      MultipartFile userProFile) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    String newUsername = request.username();
    String newEmail = request.email();
    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    Optional.ofNullable(newUsername).ifPresent(user::updateName);
    Optional.ofNullable(newEmail).ifPresent(user::updateEmail);
    Optional.ofNullable(request.password()).ifPresent(user::updatePassword);
    Optional.ofNullable(userProFile).ifPresent(profile -> {
      if(profile.isEmpty()) {
        BinaryContent binaryContent = binaryContentRepository.save(
            BinaryContent.createBinaryContent(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()));
        binaryContentStorage.put(binaryContent.getId(),convertToBytes(profile));
        user.updateProfileId(binaryContent);
      }
    });

    log.info("수정된 유저 : {}", user);

    return userMapper.entityToDto(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    Optional.ofNullable(user.getProfileId())
        .ifPresent(binaryContentRepository::deleteById);
    userStatusRepository.deleteByUserId(userId);

    userRepository.deleteById(userId);
  }

  =

  private byte[] convertToBytes(MultipartFile imageFile) {
    try {
      return imageFile.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to convert file to byte array", e);
    }
  }
}
