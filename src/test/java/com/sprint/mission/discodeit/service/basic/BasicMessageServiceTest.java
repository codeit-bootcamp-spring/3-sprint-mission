package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verify;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void create_success() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest("hello", channelId, authorId);
        BinaryContentCreateRequest binaryReq = new BinaryContentCreateRequest(
            "test.txt", "text/plain", new byte[]{1});
        List<BinaryContentCreateRequest> binaryList = List.of(binaryReq);

        // 모든 mock 객체 미리 선언 (중요!)
        Channel mockChannel = mock(Channel.class);
        User mockUser = mock(User.class);
        BinaryContent mockBinaryContent = mock(BinaryContent.class);
        Message savedMessage = mock(Message.class);
        MessageDto expectedDto = mock(MessageDto.class);

        // mock 동작 정의
        given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(mockUser));
        given(binaryContentRepository.save(any())).willReturn(mockBinaryContent);
        given(messageRepository.save(any())).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, binaryList);

        // then
        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
        then(messageMapper).should().toDto(any(Message.class));

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("메시지 단건 조회 성공")
    void find_success() {
        // given
        UUID id = UUID.randomUUID();
        Message message = mock(Message.class);
        MessageDto dto = mock(MessageDto.class);

        given(messageRepository.findById(id)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(dto);

        // when
        MessageDto result = messageService.find(id);

        // then
        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("메시지 단건 조회 실패")
    void find_fail() {
        // given
        UUID id = UUID.randomUUID();
        given(messageRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.find(id))
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() {
        // given
        UUID id = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("new content");

        Message message = mock(Message.class);
        MessageDto messageDto = mock(MessageDto.class);

        given(messageRepository.findById(id)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(messageDto);

        // when
        MessageDto result = messageService.update(id, request);

        // then
        verify(message).update("new content");
        assertThat(result).isEqualTo(messageDto);
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void delete_success() {
        // given
        UUID id = UUID.randomUUID();
        given(messageRepository.existsById(id)).willReturn(true);

        // when
        messageService.delete(id);

        // then
        then(messageRepository).should().deleteById(id);
    }

    @Test
    @DisplayName("메시지 삭제 실패")
    void delete_fail() {
        // given
        UUID id = UUID.randomUUID();
        given(messageRepository.existsById(id)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> messageService.delete(id))
            .isInstanceOf(MessageNotFoundException.class);
    }
}