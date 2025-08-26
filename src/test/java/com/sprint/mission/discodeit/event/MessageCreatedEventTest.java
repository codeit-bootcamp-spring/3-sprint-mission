package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.Test;

class MessageCreatedEventTest {

  @Test
  void eventShouldContainMessage() {
    User author = User.create("author@email.com", "author", "pw", null);
    Channel channel = Channel.createPublic("채널", "테스트 채널");
    Message message = Message.create("내용", author, channel);
    MessageCreatedEvent event = new MessageCreatedEvent(message);
    assertThat(event.message()).isEqualTo(message);
  }
}
