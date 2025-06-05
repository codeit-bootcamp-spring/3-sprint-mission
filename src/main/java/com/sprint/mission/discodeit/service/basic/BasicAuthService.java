package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.AuthException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final UserStatusMapper userStatusMapper;

  public UserResponse login(String username, String password) throws AuthException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(AuthException::usernameNotFound);

    if (!user.getPassword().equals(password)) {
      throw AuthException.invalidPassword();
    }

    user.getUserStatus().updateLastActiveAt();
    userRepository.save(user);

    return userMapper.toResponse(user);
  }
}