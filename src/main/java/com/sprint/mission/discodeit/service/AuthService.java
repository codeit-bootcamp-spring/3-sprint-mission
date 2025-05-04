package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.repository.UserRepository;

public class AuthService {

  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto login(LoginRequest request) {
    return userRepository.findAll().stream()
        .filter(user -> user.getUsername().equals(request.username()))
        .filter(user -> user.getPassword().equals(request.password()))  // 단순 비교
        .findFirst() // 조건 만족하는 첫 번째 유저
        .map(user -> new UserDto(user, false, false))  // UserDto로 변환하여 비밀번호 정보 제외
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 username & password 입니다."));
  }
}
