package com.sprint.mission.discodeit.security.userdetails;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));

    BinaryContentResponse profile = user.getProfile() != null
        ? BinaryContentResponse.from(user.getProfile())
        : null;
    // 온라인 여부는 JwtRegistry 기반 상위 서비스 계층에서 재계산
    boolean online = false;

    UserResponse userResponse = new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profile,
        online,
        user.getRole()
    );

    UserDetails userDetails = new DiscodeitUserDetails(userResponse, user.getPassword());
    log.debug("UserDetails 기본 구현체: {}", userDetails.getClass());
    return userDetails;
  }
}
