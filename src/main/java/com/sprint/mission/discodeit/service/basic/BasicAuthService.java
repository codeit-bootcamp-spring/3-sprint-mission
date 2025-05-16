package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.Dto.authService.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       : BasicAuthService
 * author         : doungukkim date           : 2025. 4. 25. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 25.        doungukkim 최초 생성
 */

@Primary
@RequiredArgsConstructor
@Service("basicAuthService")
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;


  public ResponseEntity<?> login(LoginRequest request) {

    String username = request.username();
    String password = request.password();

    List<User> users = userRepository.findAllUsers();
    List<User> selectedUser = users.stream().filter(user -> user.getUsername().equals(username))
        .toList();

    if (selectedUser.isEmpty()) {
      return ResponseEntity.status(404).body("User with username " + username + " not found");
    }

    if ((selectedUser.size() == 1) && (selectedUser.get(0).getPassword().equals(password))) {

      LoginResponse loginResponse = new LoginResponse(
          selectedUser.get(0).getId(),
          selectedUser.get(0).getCreatedAt(),
          selectedUser.get(0).getUpdatedAt(),
          selectedUser.get(0).getUsername(),
          selectedUser.get(0).getEmail(),
          selectedUser.get(0).getPassword(),
          selectedUser.get(0).getProfileId()
      );
      return ResponseEntity
          .status(200)
          .body(loginResponse);
    }
    if (selectedUser.size() >= 2) {
      return ResponseEntity.status(404).body("USERNAME IS NOT UNIQUE I AM IN TROUBLE!!!");
    }
    return ResponseEntity.status(400).body("wrong password");
  }
}
