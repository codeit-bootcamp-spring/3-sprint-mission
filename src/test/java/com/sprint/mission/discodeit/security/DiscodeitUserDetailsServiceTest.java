package com.sprint.mission.discodeit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class DiscodeitUserDetailsServiceMockTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DiscodeitUserDetailsService userDetailsService;

  @Test
  void loadUserByUsername_정상_동작_확인() {
    String username = "mockuser";
    User mockUser = User.create("mock@email.com", username, "mockpwd", null);

    given(userRepository.findByUsername(username)).willReturn(Optional.of(mockUser));

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    assertNotNull(userDetails);
    assertEquals(username, userDetails.getUsername());
  }
}
