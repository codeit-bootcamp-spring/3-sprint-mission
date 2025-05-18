package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  // 생성자를 통해 Repository 주입
  // @RequiredArgsConstructor 어노테이션이 자동으로 생성

  @Override
  public User createUser(UserCreateRequest userRequest, Optional<BinaryContentCreateRequest> profileRequest) {
    String username = userRequest.username();
    String email = userRequest.email();

    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }

    if (isNameDuplicate(username)) {
      throw new IllegalArgumentException("이미 존재하는 이름입니다: " + username);
    }

    User user = new User(username, email, userRequest.password());
    userRepository.save(user);

    // UserStatus 생성
    UserStatus status = new UserStatus(user.getId());
    userStatusRepository.save(status);

    // Optional 프로필 이미지 처리
    profileRequest
        .filter(BinaryContentCreateRequest::isValid)
        .ifPresent(profile -> {
          BinaryContent binaryContent = new BinaryContent(profile.fileName(), user.getId());
          binaryContentRepository.save(binaryContent);
          user.setProfileId(binaryContent.getId());
          userRepository.save(user);
        });

    return user;
  }


  @Override
  public Optional<UserDto> find(UUID id) {
    return userRepository.findById(id)
        .map(user -> {
          boolean hasProfileImage = binaryContentRepository.findByUserId(user.getId()).isPresent();
          boolean isOnline = userStatusRepository.find(user.getId())
              .map(UserStatus::checkUserConnect)
              .orElse(false);
          return new UserDto(user, hasProfileImage, isOnline);
        });
  }


  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(user -> {
          boolean hasProfileImage = binaryContentRepository.findByUserId(user.getId()).isPresent();
          boolean isOnline = userStatusRepository.find(user.getId())
              .map(UserStatus::checkUserConnect)
              .orElse(false);
          return new UserDto(user, hasProfileImage, isOnline);
        })
        .collect(Collectors.toList());
  }


  @Override
  public User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + userId));

    if (userUpdateRequest.username() != null && !userUpdateRequest.username().equals(user.getUsername())) {
      if (isNameDuplicate(userUpdateRequest.username())) {
        throw new IllegalArgumentException("이미 존재하는 이름입니다: " + userUpdateRequest.username());
      }
      user.updateName(userUpdateRequest.username());  // 이름 수정 메서드 호출
    }

    if (userUpdateRequest.email() != null && !userUpdateRequest.email().equals(user.getEmail())) {
      if (isEmailDuplicate(userUpdateRequest.email())) {
        throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userUpdateRequest.email());
      }
      user.updateEmail(userUpdateRequest.email());  // 이메일 수정 메서드 호출
    }

    profileCreateRequest
        .filter(BinaryContentCreateRequest::isValid)
        .ifPresent(profile -> {
          BinaryContent binaryContent = new BinaryContent(profile.fileName(), user.getId());
          binaryContentRepository.save(binaryContent);
        });

    userRepository.update(user);
    return user;
  }



  @Override
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

    // 1. 유저가 만든 채널 삭제 및 참여 채널에서 제거
    channelRepository.deleteByOwnerId(id);  // 유저가 만든 채널을 삭제
    channelRepository.removeUserFromAllChannels(id);  // 유저가 참여한 채널에서 유저를 제거

    // 2. 프로필 이미지 및 유저 상태 정보 삭제
    binaryContentRepository.delete(id);
    userStatusRepository.delete(id);

    userRepository.delete(id);
  }

  private boolean isEmailDuplicate(String email) {
    return userRepository.findAll().stream()
        .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
  }

  private boolean isNameDuplicate(String username) {
    return userRepository.findAll().stream()
        .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
  }
}
