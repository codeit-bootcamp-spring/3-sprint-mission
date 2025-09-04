package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.assembler.MessageAssembler;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.UserOnlineService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageCreatedEventTest {

  private MessageAssembler messageAssembler;

  @BeforeEach
  void setUp() {
    var userMapper = mock(UserMapper.class);
    var userOnlineService = mock(UserOnlineService.class);
    var binaryContentMapper = mock(BinaryContentMapper.class);
    messageAssembler = new MessageAssembler(userMapper, userOnlineService, binaryContentMapper);
  }

  @Test
  void 이벤트는_메시지를_포함해야_한다() {
    User author = UserFixture.createValidUser();
    Channel channel = ChannelFixture.createPublic();
    Message message = MessageFixture.createCustom("내용", author, channel);
    MessageResponse response = new MessageResponse(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        null,
        List.of()
    );
    MessageCreatedEvent event = new MessageCreatedEvent(response);
    assertThat(event.message()).isEqualTo(response);
  }
}
