package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public User login(LoginRequest request) {
    //1. Username으로 user 찾기
    User user = userRepository.findByUsername(request.username())
        .orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("user", ErrorMessages.ERROR_NOT_FOUND)));

    //2. password 일치 여부 확인
    if (!user.getPassword().equals(request.password())) {
      throw new IllegalArgumentException(
          ErrorMessages.format("password", ErrorMessages.ERROR_MISMATCH));
    }
    return user;
  }
}
