package com.sprint.mission.discodeit.service.message;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.assembler.MessageAssembler;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
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

      then(messageRepository).should().save(any(Message.class));
    }
  }

  @Nested
  class Delete {

    @Test
    void 메시지를_삭제하면_해당_ID로_삭제_요청된다() {
      Message message = mock(Message.class);
      given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));

      messageService.delete(message.getId());

      then(messageRepository).should().deleteById(message.getId());
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

  @Nested
  class ExceptionCases {

    @Test
    void 존재하지_않는_유저로_메시지_생성시_예외() {
      CreateMessageCommand command = mock(CreateMessageCommand.class);
      given(command.authorId()).willReturn(userId);
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> messageService.create(command)).isInstanceOf(
          UserNotFoundException.class);
    }

    @Test
    void 존재하지_않는_채널로_메시지_생성시_예외() {
      User user = UserFixture.createValidUser();
      CreateMessageCommand command = mock(CreateMessageCommand.class);
      given(command.authorId()).willReturn(userId);
      given(command.channelId()).willReturn(channelId);
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(channelRepository.findById(channelId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> messageService.create(command)).isInstanceOf(
          ChannelNotFoundException.class);
    }

    @Test
    void 존재하지_않는_메시지_삭제시_예외() {
      UUID messageId = UUID.randomUUID();
      given(messageRepository.findById(messageId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> messageService.delete(messageId)).isInstanceOf(
          MessageNotFoundException.class);
    }

    @Test
    void 존재하지_않는_메시지_업데이트시_예외() {
      UUID messageId = UUID.randomUUID();
      given(messageRepository.findById(messageId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> messageService.updateContent(messageId, "fail"))
          .isInstanceOf(MessageNotFoundException.class);
    }
  }

  @Nested
  class FindByChannelId {

    @Test
    void 채널ID로_메시지_목록_조회_성공() {
      Message message1 = MessageFixture.createValid();
      Message message2 = MessageFixture.createValid();
      given(messageRepository.findAllByChannelId(channelId)).willReturn(
          List.of(message1, message2));
      given(messageAssembler.toResponse(any())).willReturn(mock(MessageResponse.class));
      List<MessageResponse> result = messageService.findAllByChannelId(channelId);

      assertThat(result).hasSize(2);
    }
  }
}
