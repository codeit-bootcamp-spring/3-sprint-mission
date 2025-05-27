package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusService userStatusService;


  @Override
  public User create(CreateUserRequest createUserRequest,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    if (isUsernameDuplicate(createUserRequest.username())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 사용자 이름입니다.");
    }
    if (isEmailDuplicate(createUserRequest.email())) {
      throw new IllegalArgumentException("[error] 이미 존재하는 E-mail입니다.");
    }

    UUID nullableProfileId = binaryContentRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.type();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = binaryContentRepository.save(new BinaryContent(fileName,
              contentType, (long) bytes.length, bytes));
          return binaryContentRepository.save(binaryContent).getId();
        })
        .orElse(null);

    User user = new User(createUserRequest.username(), createUserRequest.email(),
        createUserRequest.password(),
        nullableProfileId);
    User createdUser = userRepository.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser.getId(), now);
    userStatusRepository.save(userStatus);

    return createdUser;
  }

  @Override
  public User find(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll()
        .stream()
        .toList();
  }

  @Override
  public User update(UUID userId, UpdateUserRequest request,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = request.newUsername();
    String newEmail = request.newEmail();
    if (isEmailDuplicate(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (isUsernameDuplicate(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    UUID nullableProfileId = binaryContentRequest
        .map(profileRequest -> {
          Optional.ofNullable(user.getProfileId())
              .ifPresent(binaryContentRepository::deleteById);

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.type();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName,
              contentType, (long) bytes.length, bytes);
          return binaryContentRepository.save(binaryContent).getId();
        })
        .orElse(null);

    String newPassword = request.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfileId);

    return userRepository.save(user);
  }


  public User update(UUID userId, UpdateUserRequest request,
      CreateBinaryContentRequest profileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = request.newUsername();
    String newEmail = request.newEmail();
    if (isEmailDuplicate(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (isUsernameDuplicate(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    BinaryContent binaryContent = new BinaryContent(profileCreateRequest.fileName(),
        profileCreateRequest.type(),
        (long) profileCreateRequest.bytes().length,
        profileCreateRequest.bytes());

    UUID profileId = binaryContentRepository.save(binaryContent).getId();

    String newPassword = request.newPassword();
    user.update(newUsername, newEmail, newPassword, profileId);

    return userRepository.save(user);

  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    // 관련 도메인도 삭제
    Optional.ofNullable(user.getProfileId())
        .ifPresent(binaryContentRepository::deleteById);
    userStatusRepository.deleteByUserId(userId);
    userRepository.deleteById(userId);
    System.out.println("delete user : " + userId + " success.");

  }

  // username 중복 방지
  private boolean isUsernameDuplicate(String username) {
    return userRepository
        .findAll()
        .stream()
        .anyMatch(user -> user.getUsername().equals(username));
  }

  // email 중복 방지
  private boolean isEmailDuplicate(String email) {
    return userRepository
        .findAll()
        .stream()
        .anyMatch(user -> user.getEmail().equals(email));
  }


}
