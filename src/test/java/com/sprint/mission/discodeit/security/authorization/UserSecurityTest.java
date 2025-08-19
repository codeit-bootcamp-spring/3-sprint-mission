package com.sprint.mission.discodeit.security.authorization;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class UserSecurityTest {

  UserSecurity userSecurity = new UserSecurity();

  @Test
  void 본인_계정이면_true() {
    UUID id = UUID.randomUUID();
    setAuthentication(id);
    assertThat(userSecurity.isSelf(id)).isTrue();
  }

  @Test
  void 타인_계정이면_false() {
    setAuthentication(UUID.randomUUID());
    assertThat(userSecurity.isSelf(UUID.randomUUID())).isFalse();
  }

  @Test
  void 인증_없으면_false() {
    SecurityContextHolder.clearContext();
    assertThat(userSecurity.isSelf(UUID.randomUUID())).isFalse();
  }

  private void setAuthentication(UUID userId) {
    UserResponse dummy = new UserResponse(userId, "u", "e@test.com", null, true, null);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(dummy, "pwd");
    var auth = new UsernamePasswordAuthenticationToken(principal, "pwd", Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
