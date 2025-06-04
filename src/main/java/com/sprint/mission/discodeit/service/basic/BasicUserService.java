package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BaseException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    valid(username, email);

    BinaryContent profile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(
                  fileName,
                  (long) bytes.length,
                  contentType
          );
          BinaryContent saveBinaryContent = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(saveBinaryContent.getId(), bytes);
          return saveBinaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, profile);
    User createdUser = userRepository.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser, now);

    createdUser.setUserStatus(userStatus);

    return userMapper.toDto(createdUser);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(UserNotFoundException::new);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    valid(newUsername, newEmail);

    BinaryContent userProfile = user.getProfile();

    BinaryContent nullableProfile = optionalProfileCreateRequest.map(profileRequest -> {
      Optional.ofNullable(userProfile)
              .map(BinaryContent::getId)
              .ifPresent(binaryContentRepository::deleteById);

      String fileName = profileRequest.fileName();
      String contentType = profileRequest.contentType();
      byte[] bytes = profileRequest.bytes();

      BinaryContent binaryContent = new BinaryContent(
              fileName,
              (long) bytes.length,
              contentType
      );
      binaryContentRepository.save(binaryContent);
      binaryContentStorage.put(binaryContent.getId(), bytes);
      return binaryContent;
    }).orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    Optional.ofNullable(user.getProfile())
            .ifPresent(profile -> {
              binaryContentRepository.deleteById(profile.getId());
              binaryContentStorage.put(profile.getId(), null);
            });

    userRepository.deleteById(userId);
  }

  private void valid(String newUsername, String newEmail) {
    if (userRepository.existsByEmail(newEmail)) {
      throw new BaseException() {
        @Override
        public String getCode() {
          return "Duplicate Email";
        }

        @Override
        public String getMessage() {
          return "Duplicate email: " + newEmail;
        }

        @Override
        public HttpStatus getHttpStatus() {
          return HttpStatus.CONFLICT;
        }
      };
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new BaseException() {
        @Override
        public String getCode() {
          return "Duplicate Username";
        }

        @Override
        public String getMessage() {
          return "Duplicate username: " + newUsername;
        }

        @Override
        public HttpStatus getHttpStatus() {
          return HttpStatus.CONFLICT;
        }
      };
    }
  }
}
