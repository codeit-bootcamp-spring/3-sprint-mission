package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public User create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest
  ) {

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("이미 존재하는 username입니다.");
    }

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 존재하는 email입니다.");
    }

    BinaryContent nullableProfile = profileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          BinaryContent binaryContent =
              BinaryContent.builder()
                  .fileName(fileName)
                  .size((long) bytes.length)
                  .bytes(bytes)
                  .contentType(contentType)
                  .build();

          return binaryContentRepository.save(binaryContent);
        })
        .orElse(null);

    String password = userCreateRequest.password();

    User user =
        User.builder()
            .username(username)
            .email(email)
            .password(password)
            .profile(nullableProfile)
            .build();

    System.out.println("user save 진행!!");
    userRepository.save(user);
    System.out.println("user save 진행 됐어용~~");
    System.out.println("user = " + user);

    Instant now = Instant.now();
    UserStatus userStatus =
        UserStatus.builder()
            .user(user)
            .lastActiveAt(now)
            .build();

    userStatusRepository.save(userStatus);

    return user;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO find(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO findByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
  }


  @Override
  @Transactional(readOnly = true)
  public UserDTO findByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDTO> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDTO)
        .toList();
  }

  @Override
  public User update(UUID userId, UserUpdateRequest userUpdateDTO,
      Optional<BinaryContentCreateRequest> optionalProfileCreateDTO) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

    String newUsername = userUpdateDTO.newUsername();
    String newEmail = userUpdateDTO.newEmail();

    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("이미 존재하는 유저 아이디입니다.");
    }

    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    BinaryContent nullableProfile = optionalProfileCreateDTO
        .map(profileRequest -> {
          Optional.ofNullable(user.getProfile().getId())
              .ifPresent(binaryContentRepository::deleteById);

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          BinaryContent binaryContent =
              BinaryContent.builder()
                  .fileName(fileName)
                  .bytes(bytes)
                  .contentType(contentType)
                  .build();

          return binaryContentRepository.save(binaryContent);
        })
        .orElse(null);

    String newPassword = userUpdateDTO.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);

    return user;
  }


  @Override
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

    Optional.ofNullable(user.getProfile().getId())
        .ifPresent(binaryContentRepository::deleteById);
    userStatusRepository.deleteByUserId(id);
    userRepository.deleteById(id);
  }
}
