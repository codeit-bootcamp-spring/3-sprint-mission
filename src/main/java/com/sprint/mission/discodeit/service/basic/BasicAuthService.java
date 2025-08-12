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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final SessionRegistry sessionRegistry;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto getCurrentUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest) {
    User user = userRepository.findById(roleUpdateRequest.userId()).orElseThrow(UserNotFoundException::new);
    user.updateRole(roleUpdateRequest.newRole());
    invalidateSession(user.getUsername());
    userRepository.save(user);
    return userMapper.toDto(user);
  }

  private void invalidateSession(String username) {
      List<Object> principals = sessionRegistry.getAllPrincipals();
      for(Object principal : principals){
      UserDetails userDetails = (UserDetails) principal;
      String principalName = userDetails.getUsername();
      if(principalName.equals(username)){
        List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principal, false);
        for(SessionInformation session : allSessions){
          session.expireNow();
        }
        break;
      }
    }
  }
}
