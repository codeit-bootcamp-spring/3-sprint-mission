package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public User create(UserCreateRequest userCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();
    String password = userCreateRequest.password();

    if (userRepository.isExistUsername(username)) {
      System.out.println("생성 실패 : 이미 존재하는 이름입니다.");
      return null;
    } else if (userRepository.isExistEmail(email)) {
      System.out.println("생성 실패 : 이미 존재하는 E-mail입니다.");
      return null;
    } else {
      User user = new User(username, email, password);
      userRepository.save(user);

      UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
      userStatusRepository.save(userStatus);
      return user;
    }
  }

  @Override
  public User create(UserCreateRequest userCreateRequest,
      BinaryContentCreateRequest binaryContentCreateRequest) {
    User user = this.create(userCreateRequest);
    if (user != null) {
      BinaryContent binaryForPortrait = new BinaryContent(
          binaryContentCreateRequest.fileName(),
          (long) binaryContentCreateRequest.content().length,
          binaryContentCreateRequest.contentType(),
          binaryContentCreateRequest.content()
      );
      user.setPortraitId(binaryContentRepository.save(binaryForPortrait).getId());
      userRepository.save(user);
      System.out.println(
          "생성 성공 : " + user + "\n" + userStatusRepository.findByUserId(user.getId()) + "\n"
              + user.getPortraitId());
    }
    return user;
  }

  @Override
  public UserDto findById(UUID userId) {
    User foundUser = userRepository.findById(userId);
    return toDto(foundUser);
  }

  @Override
  public UserDto findByUsername(String username) {
    User foundUser = userRepository.findAll()
        .stream()
        .filter(user1 -> user1.getUsername().equals(username)).findFirst().orElse(null);
    return toDto(foundUser);
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public User update(UUID userId, UserUpdateRequest userUpdateRequest) {
    User user = userRepository.findById(userId);

    if (user == null) {
      System.out.println("수정 실패 : 존재하지 않는 사용자입니다.");
    } else {
      boolean isUpdated = false;
      String newUsername = userUpdateRequest.newUsername();
      String newEmail = userUpdateRequest.newEmail();
      String newPassword = userUpdateRequest.newPassword();

      if (newUsername != null) {
        if (userRepository.isExistUsername(newUsername)) {
          System.out.println("수정 실패 : 이미 존재하는 사용자 이름입니다.");
        } else {
          isUpdated = true;
        }
      }

      if (newEmail != null) {
        if (userRepository.isExistEmail(newEmail)) {
          System.out.println("수정 실패 : 이미 존재하는 사용자 메일입니다.");
        } else {
          isUpdated = true;
        }
      }

      if (newPassword != null && !user.getPassword().equals(newPassword)) {
        isUpdated = true;
      }

      if (isUpdated) {
        user.update(newUsername, newEmail, newPassword);
        userRepository.save(user);
        System.out.println("수정 성공 : " + user);
      }
    }
    return user;
  }

  @Override
  public User update(UUID userId, BinaryContentCreateRequest binaryContentCreateRequest) {
    User user = userRepository.findById(userId);
    if (user != null) {
      BinaryContent binaryForPortrait = new BinaryContent(
          binaryContentCreateRequest.fileName(),
          (long) binaryContentCreateRequest.content().length,
          binaryContentCreateRequest.contentType(),
          binaryContentCreateRequest.content()
      );
      if (user.getPortraitId() != null) {
        binaryContentRepository.deleteById(user.getPortraitId());
      }
      user.setPortraitId(binaryContentRepository.save(binaryForPortrait).getId());
      userRepository.save(user);
      System.out.println(
          "\n" + userStatusRepository.findByUserId(user.getId()) + "\n" + user.getPortraitId());
    }
    return user;

  }

  @Override
  public void delete(UUID userId) {
    if (findById(userId) == null) {
      throw new NoSuchElementException("삭제 실패 : 존재하지 않는 사용자입니다.");
    }
    if (findById(userId).profileId() != null) {
      binaryContentRepository.deleteById(this.findById(userId).profileId());
    }
    userStatusRepository.deleteByUserId(userId);
    userRepository.delete(userId);
  }

  private UserDto toDto(User user) {
    boolean isOnline = userStatusRepository.findById(user.getId())
        .map(UserStatus::isOnline)
        .orElse(Boolean.FALSE);

    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getPortraitId()
    );
  }
}
