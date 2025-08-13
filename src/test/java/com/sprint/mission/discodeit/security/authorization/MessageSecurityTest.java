package com.sprint.mission.discodeit.security.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class MessageSecurityTest {

  MessageRepository messageRepository = mock(MessageRepository.class);
  MessageSecurity messageSecurity = new MessageSecurity(messageRepository);

  UUID authorId = UUID.randomUUID();
  UUID messageId = UUID.randomUUID();

  @BeforeEach
  void setup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void 작성자이면_true() {
    setAuthentication(authorId);
    Message message = Mockito.mock(Message.class);
    User author = Mockito.mock(User.class);
    when(author.getId()).thenReturn(authorId);
    when(message.getAuthor()).thenReturn(author);
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

    assertThat(messageSecurity.isAuthor(messageId)).isTrue();
  }

  @Test
  void 작성자가_아니면_false() {
    setAuthentication(authorId);
    Message message = Mockito.mock(Message.class);
    User author = Mockito.mock(User.class);
    when(author.getId()).thenReturn(UUID.randomUUID());
    when(message.getAuthor()).thenReturn(author);
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

    assertThat(messageSecurity.isAuthor(messageId)).isFalse();
  }

  @Test
  void 인증_없으면_false() {
    when(messageRepository.findById(messageId)).thenReturn(Optional.empty());
    assertThat(messageSecurity.isAuthor(messageId)).isFalse();
  }

  private void setAuthentication(UUID userId) {
    UserResponse user = new UserResponse(userId, "u", "e@test.com", null, true, null);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(user, "pwd");
    var auth = new UsernamePasswordAuthenticationToken(principal, "pwd", Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
