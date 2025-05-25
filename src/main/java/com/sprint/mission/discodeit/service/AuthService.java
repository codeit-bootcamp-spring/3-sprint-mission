package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  public UserDto login(LoginRequest request) {
    return userRepository.findAll().stream()
        .filter(user -> user.getUsername().equals(request.username()))
        .filter(user -> user.getPassword().equals(request.password()))
        .findFirst()
        .map(user -> {
          boolean hasProfileImage = binaryContentRepository.findByUserId(user.getId()).isPresent();
          boolean isOnline = userStatusRepository.find(user.getId())
              .map(UserStatus::isOnline)
              .orElse(false);

          return new UserDto(user, hasProfileImage, isOnline);
        })
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 username & password 입니다."));
  }
}
