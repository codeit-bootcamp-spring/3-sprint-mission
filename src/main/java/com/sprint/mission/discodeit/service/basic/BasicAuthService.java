package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.LoginFailedException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserStatusException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserResponseDTO login(LoginDTO loginDTO) {
    String name = loginDTO.username();
    String password = loginDTO.password();

    User user = userRepository.findByName(name)
        .orElseThrow(() -> new NotFoundUserException("name이 " + name + "인 사용자를 찾을 수 없습니다."));

    if (!user.getPassword().equals(password)) {
      throw new LoginFailedException();
    }

    // User 로그인 시 User 온라인 상태 정보 변경
    user.updateOnline(true);
    UserStatus userStatus = findUserStatus(user.getId());
    userStatus.updatelastActiveAt(Instant.now());
    userRepository.save(user);
    userStatusRepository.save(userStatus);

    return User.toDTO(user);
  }

  private UserStatus findUserStatus(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .orElseThrow(NotFoundUserStatusException::new);
  }
}
