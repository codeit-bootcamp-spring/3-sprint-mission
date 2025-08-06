package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto getCurrentUserInfo(UserDetails userDetails) {
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

    return userMapper.toDto(user);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest) {
    User user = userRepository.findById(roleUpdateRequest.userId()).orElseThrow(UserNotFoundException::new);
    user.updateRole(roleUpdateRequest.newRole());
    userRepository.save(user);
    return userMapper.toDto(user);
  }
}
