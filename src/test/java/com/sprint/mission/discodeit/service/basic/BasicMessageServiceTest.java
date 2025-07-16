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
        BinaryContentCreateRequest binaryReq = new BinaryContentCreateRequest("test.txt",
            "text/plain", new byte[]{1});
        List<BinaryContentCreateRequest> binaryList = List.of(binaryReq);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(mock(Channel.class)));
        given(userRepository.findById(authorId)).willReturn(Optional.of(mock(User.class)));
        given(messageRepository.save(any())).willReturn(mock(Message.class));
        given(messageMapper.toDto(any(Message.class))).willReturn(mock(MessageDto.class));

        // when
        MessageDto result = messageService.create(request, binaryList);

        // then
        assertThat(result).isEqualTo(mock(MessageDto.class));
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 단건 조회 성공")
    void find_success() {
        // given
        UUID id = UUID.randomUUID();

        given(messageRepository.findById(id)).willReturn(Optional.of(mock(Message.class)));
        given(messageMapper.toDto(mock(Message.class))).willReturn(mock(MessageDto.class));

        // when
        MessageDto result = messageService.find(id);

        // then
        assertThat(result).isEqualTo(mock(MessageDto.class));
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

        given(messageRepository.findById(id)).willReturn(Optional.of(mock(Message.class)));
        given(messageMapper.toDto(mock(Message.class))).willReturn(mock(MessageDto.class));

        // when
        MessageDto result = messageService.update(id, request);

        // then
        verify(mock(Message.class)).update("new content");
        assertThat(result).isEqualTo(mock(MessageDto.class));
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