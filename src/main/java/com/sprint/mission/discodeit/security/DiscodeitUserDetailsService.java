package com.sprint.mission.discodeit.security;

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
    boolean online = user.getUserStatus() != null && user.getUserStatus().isOnline();

    UserResponse userResponse = new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profile,
        online
    );

    UserDetails userDetails = new DiscodeitUserDetails(userResponse, user.getPassword());
    log.debug("UserDetails 기본 구현체: {}", userDetails.getClass());
    return userDetails;
  }
}
