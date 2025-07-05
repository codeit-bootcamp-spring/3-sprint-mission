package com.sprint.mission.discodeit.service.jcf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.Test;

class JCFChannelServiceTest {

  @Test
  void createChannel() {
  }

  @Test
  void getChannelById() {
  }

  @Test
  void searchChannels() {
  }

  @Test
  void getUserChannels() {
  }

  @Test
  void updateChannelName() {
    // given
    User user = User.create("user1@example.com", "홍길동", "password1");
    Channel channel = Channel.create(user, "개발자 모임");

    // when
    channel.updateName("개발자 모임쓰");
    String newName = channel.getName();

    // then
    assertEquals(channel.getName(), newName);
  }

  @Test
  void addParticipant() {
  }

  @Test
  void removeParticipant() {
  }

  @Test
  void deleteChannel() {
  }
}