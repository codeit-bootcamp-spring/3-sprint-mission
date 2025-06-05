package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.assembler.MessageAssembler;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private MessageAssembler messageAssembler;

  @InjectMocks
  private BasicMessageService messageService;

  private final UUID userId = UUID.randomUUID();
  private final UUID channelId = UUID.randomUUID();

  @Nested
  class Create {

    @Test
    void 메시지를_생성하면_메시지_엔티티가_저장된다() {
      User user = UserFixture.createValidUser();
      Channel channel = ChannelFixture.createPublic();

      CreateMessageCommand command = mock(CreateMessageCommand.class);
      given(command.authorId()).willReturn(userId);
      given(command.channelId()).willReturn(channelId);
      given(command.content()).willReturn("test message");

      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

      messageService.create(command);

      verify(messageRepository).save(any(Message.class));
    }
  }

  @Nested
  class Delete {

    @Test
    void 메시지를_삭제하면_해당_ID로_삭제_요청된다() {
      Message message = mock(Message.class);
      given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));

      messageService.delete(message.getId());

      verify(messageRepository).deleteById(message.getId());
    }
  }

  @Nested
  class Update {

    @Test
    void 메시지를_업데이트하면_내용이_수정된다() {
      UUID messageId = UUID.randomUUID();
      Message message = MessageFixture.createValid();
      message.assignIdForTest(messageId);

      given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));
      given(messageRepository.save(any())).willReturn(message);
      given(messageAssembler.toResponse(any())).willReturn(mock(MessageResponse.class));

      String updatedContent = "Updated content";
      messageService.updateContent(message.getId(), updatedContent);

      assertThat(message.getContent()).isEqualTo(updatedContent);
    }
  }
}
