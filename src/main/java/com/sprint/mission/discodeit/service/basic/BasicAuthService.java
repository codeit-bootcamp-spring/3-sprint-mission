package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.AuthException;
import com.sprint.mission.discodeit.dto.data.UserResponse;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserResponse login(LoginRequest request) {
    User user = userRepository.findByNameWithPassword(request.userName(), request.password())
        .orElseThrow(AuthException::invalidCredentials);

    return toUserResponse(user);
  }
  
  private UserResponse toUserResponse(User user) {
    Optional<UserStatus> userStatus = userStatusRepository.findById(user.getId());
    return UserResponse.from(user, userStatus);
  }
}