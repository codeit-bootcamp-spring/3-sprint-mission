package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.mapper.PageMapper;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.sprint.mission.discodeit.testutil.TestConstants.*;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicMessageService 단위 테스트")
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private MapperFacade mapperFacade;

  @Mock
  private PageMapper pageMapper;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공 - 첨부파일 없음")
  void createMessage_Success_WithoutAttachments() {
    // Given
    MessageCreateRequest request = createMessageCreateRequest();
    Channel channel = createPublicChannel();
    User author = createDefaultUser();
    Message savedMessage = createMessage();
    MessageDto expectedDto = createMessageDto();

    given(channelRepository.findById(CHANNEL_ID_1)).willReturn(Optional.of(channel));
    given(userRepository.findById(USER_ID_1)).willReturn(Optional.of(author));
    given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
    given(mapperFacade.toDto(savedMessage)).willReturn(expectedDto);

    // When
    MessageDto result = messageService.create(request);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(channelRepository).should().findById(CHANNEL_ID_1);
    then(userRepository).should().findById(USER_ID_1);
    then(messageRepository).should().save(any(Message.class));
    then(mapperFacade).should().toDto(savedMessage);
  }

  @Test
  @DisplayName("메시지 생성 성공 - 첨부파일 포함")
  void createMessage_Success_WithAttachments() {
    // Given
    MessageCreateRequest request = createMessageCreateRequest();
    List<BinaryContentCreateRequest> attachmentRequests = Arrays.asList(createBinaryContentCreateRequest());
    Channel channel = createPublicChannel();
    User author = createDefaultUser();
    Message savedMessage = createMessage();
    MessageDto expectedDto = createMessageDto();

    given(channelRepository.findById(CHANNEL_ID_1)).willReturn(Optional.of(channel));
    given(userRepository.findById(USER_ID_1)).willReturn(Optional.of(author));
    given(binaryContentService.createAll(attachmentRequests)).willReturn(Collections.emptyList());
    given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
    given(mapperFacade.toDto(savedMessage)).willReturn(expectedDto);

    // When
    MessageDto result = messageService.create(request, attachmentRequests);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(channelRepository).should().findById(CHANNEL_ID_1);
    then(userRepository).should().findById(USER_ID_1);
    then(binaryContentService).should().createAll(attachmentRequests);
    then(messageRepository).should().save(any(Message.class));
    then(mapperFacade).should().toDto(savedMessage);
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
  void createMessage_Fail_ChannelNotFound() {
    // Given
    MessageCreateRequest request = createMessageCreateRequest(TEST_MESSAGE_CONTENT, NON_EXISTENT_CHANNEL_ID, USER_ID_1);
    given(channelRepository.findById(NON_EXISTENT_CHANNEL_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> messageService.create(request))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should().findById(NON_EXISTENT_CHANNEL_ID);
    then(userRepository).should(never()).findById(any());
    then(messageRepository).should(never()).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 사용자")
  void createMessage_Fail_UserNotFound() {
    // Given
    MessageCreateRequest request = createMessageCreateRequest(TEST_MESSAGE_CONTENT, CHANNEL_ID_1, NON_EXISTENT_USER_ID);
    Channel channel = createPublicChannel();

    given(channelRepository.findById(CHANNEL_ID_1)).willReturn(Optional.of(channel));
    given(userRepository.findById(NON_EXISTENT_USER_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> messageService.create(request))
        .isInstanceOf(UserNotFoundException.class);

    then(channelRepository).should().findById(CHANNEL_ID_1);
    then(userRepository).should().findById(NON_EXISTENT_USER_ID);
    then(messageRepository).should(never()).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_Success() {
    // Given
    Message existingMessage = createMessage();
    MessageUpdateRequest request = createMessageUpdateRequest();
    MessageDto expectedDto = createMessageDto();

    given(messageRepository.findById(MESSAGE_ID_1)).willReturn(Optional.of(existingMessage));
    given(mapperFacade.toDto(existingMessage)).willReturn(expectedDto);

    // When
    MessageDto result = messageService.update(MESSAGE_ID_1, request);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(messageRepository).should().findById(MESSAGE_ID_1);
    then(mapperFacade).should().toDto(existingMessage);
  }

  @Test
  @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
  void updateMessage_Fail_MessageNotFound() {
    // Given
    MessageUpdateRequest request = createMessageUpdateRequest();
    given(messageRepository.findById(NON_EXISTENT_MESSAGE_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> messageService.update(NON_EXISTENT_MESSAGE_ID, request))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageRepository).should().findById(NON_EXISTENT_MESSAGE_ID);
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_Success() {
    // Given
    Message existingMessage = createMessage();
    given(messageRepository.findById(MESSAGE_ID_1)).willReturn(Optional.of(existingMessage));

    // When
    messageService.delete(MESSAGE_ID_1);

    // Then
    then(messageRepository).should().findById(MESSAGE_ID_1);
    then(messageRepository).should().delete(existingMessage);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
  void deleteMessage_Fail_MessageNotFound() {
    // Given
    given(messageRepository.findById(NON_EXISTENT_MESSAGE_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> messageService.delete(NON_EXISTENT_MESSAGE_ID))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageRepository).should().findById(NON_EXISTENT_MESSAGE_ID);
    then(messageRepository).should(never()).delete(any(Message.class));
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 성공")
  void findAllByChannelId_Success() {
    // Given
    List<Message> messages = createMessageList();
    List<MessageDto> expectedDtos = createMessageDtoList();

    given(messageRepository.findAllByChannelIdWithAuthorAndAttachmentsOrderByCreatedAtAsc(CHANNEL_ID_1))
        .willReturn(messages);
    given(mapperFacade.toMessageDtoList(messages)).willReturn(expectedDtos);

    // When
    List<MessageDto> result = messageService.findAllByChannelId(CHANNEL_ID_1);

    // Then
    assertThat(result).isEqualTo(expectedDtos);
    then(messageRepository).should().findAllByChannelIdWithAuthorAndAttachmentsOrderByCreatedAtAsc(CHANNEL_ID_1);
    then(mapperFacade).should().toMessageDtoList(messages);
  }

  @Test
  @DisplayName("메시지 조회 성공")
  void findMessage_Success() {
    // Given
    Message existingMessage = createMessage();
    MessageDto expectedDto = createMessageDto();

    given(messageRepository.findById(MESSAGE_ID_1)).willReturn(Optional.of(existingMessage));
    given(mapperFacade.toDto(existingMessage)).willReturn(expectedDto);

    // When
    MessageDto result = messageService.find(MESSAGE_ID_1);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(messageRepository).should().findById(MESSAGE_ID_1);
    then(mapperFacade).should().toDto(existingMessage);
  }

  @Test
  @DisplayName("메시지 조회 실패 - 존재하지 않는 메시지")
  void findMessage_Fail_MessageNotFound() {
    // Given
    given(messageRepository.findById(NON_EXISTENT_MESSAGE_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> messageService.find(NON_EXISTENT_MESSAGE_ID))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageRepository).should().findById(NON_EXISTENT_MESSAGE_ID);
  }
}