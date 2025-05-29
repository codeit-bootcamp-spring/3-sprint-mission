package com.sprint.mission.discodeit.service.basic;

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
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
  public UserResponse create(UserRequest request, MultipartFile userProfile) {

    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("User with email already exists");
    }
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("User with username already exists");
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
    User newUser = userRepository.save(User.createUser(
        request.username(), request.email(), request.password(), newProFile));
    UserStatus newUserStatus = userStatusRepository.save(UserStatus.createUserStatus(newUser));
    newUser.updateUserStatus(newUserStatus);

    log.info("생성된 유저 : {}", newUser);

    return userMapper.entityToDto(newUser);
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
  public UserResponse update(UUID id, UserRequest.Update request, MultipartFile userProfile) {
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

    Optional.ofNullable(newUsername).ifPresent(user::updateName);
    Optional.ofNullable(newEmail).ifPresent(user::updateEmail);
    Optional.ofNullable(request.newPassword()).ifPresent(user::updatePassword);
    Optional.ofNullable(userProfile).ifPresent(profile -> {
      if (!profile.isEmpty()) {
        BinaryContent binaryContent = binaryContentRepository.save(
            BinaryContent.createBinaryContent(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()));
        binaryContentStorage.put(binaryContent.getId(), convertToBytes(profile));
        user.updateProfileId(binaryContent);
      }
    });

    log.info("수정된 유저 : {}", user);

    return userMapper.entityToDto(user);
  }

  @Override
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    userStatusRepository.deleteByUserId(id);

    userRepository.deleteById(id);
  }


  private byte[] convertToBytes(MultipartFile imageFile) {
    try {
      return imageFile.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to convert file to byte array", e);
    }
  }
}
