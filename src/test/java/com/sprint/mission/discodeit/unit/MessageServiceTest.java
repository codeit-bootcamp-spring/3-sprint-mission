package com.sprint.mission.discodeit.unit;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.AdvancedJpaPageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaMessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.advanced.MessageMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import com.sprint.mission.discodeit.unit.basic.BasicMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.mockito.BDDMockito.*;

/**
 * PackageName  : com.sprint.mission.discodeit.service
 * FileName     : MessageServiceTest
 * Author       : dounguk
 * Date         : 2025. 6. 20.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Message unit 테스트")
public class MessageServiceTest {
    @InjectMocks
    private BasicMessageService messageService;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private JpaMessageRepository messageRepository;

    @Mock
    private JpaChannelRepository channelRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaBinaryContentRepository binaryContentRepository;

    @Mock
    private LocalBinaryContentStorage binaryContentStorage;


    /**
     * 0+없는 채널일 경우 ChannelNotFoundException 반환한다.
     *  +없는 유저일 경우 UserNotFoundException을 반환한다.
     */

    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    private Channel channel;
    private User user;
    private Message message;

    @DisplayName("이미지가 있는경우 이미지 수만큼 BinaryContent를 생성한다.")
    @Test
    void whenImageAttached_thenCreateBinaryContents() {
        // given
        int numberOfFiles = 10;

        channel = new Channel();
        ReflectionTestUtils.setField(channel, "id", channelId);

        user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        MessageCreateRequest request = new MessageCreateRequest("hello world", channelId, userId);

        byte[] bytes = new byte[]{1, 2};
        List<MultipartFile> fileList = new ArrayList<>();
        MultipartFile multipartFile = new MockMultipartFile("file.png", "file.png", "image/png", bytes);
        for (int i = 0; i < numberOfFiles; i++) {
            fileList.add(multipartFile);
        }

        BinaryContent binaryContent = new BinaryContent();
        ReflectionTestUtils.setField(binaryContent, "id", UUID.randomUUID());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(messageRepository.save(any(Message.class))).willReturn(message);

        // when
        messageService.createMessage(request, fileList);

        // then
        then(binaryContentRepository).should(times(numberOfFiles)).save(any(BinaryContent.class));
        then(binaryContentStorage).should(times(numberOfFiles)).put(any(), any());
        then(messageRepository).should(times(1)).save(any(Message.class));
    }

    @DisplayName("없는 채널일 경우 ChannelNotFoundException 반환한다.")
    @Test
    void whenChannelNotFound_thenMessageThrowsChannelNotFoundException() {
        // given
        MessageCreateRequest request = new MessageCreateRequest("hello world", channelId, userId);

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when n then
        assertThatThrownBy(() -> messageService.createMessage(request, any()))
            .isInstanceOf(ChannelNotFoundException.class)
            .extracting("details", map(String.class, Object.class))
            .containsEntry("channelId", channelId);

        then(userRepository).shouldHaveNoInteractions();
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @DisplayName("없는 유저일 경우 UserNotFoundException 반환한다.")
    @Test
    void whenUserNotFound_thenMessageThrowsUserNotFoundException() {
        // given
        MessageCreateRequest request = new MessageCreateRequest("hello world", channelId, userId);

        channel = new Channel();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when n then
        assertThatThrownBy(() -> messageService.createMessage(request, any()))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("details", map(String.class, Object.class))
            .containsEntry("userId", userId);

        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }


    @DisplayName("로직에 문제가 없으면 문자가 업데이트 되어야 한다.")
    @Test
    void whenLogicIsValid_thenUpdateMessage() {
        //given
        message = Message.builder()
            .content("hello Daniel")
            .build();

        MessageUpdateRequest request = new MessageUpdateRequest("hello Paul");

        JpaMessageResponse response = JpaMessageResponse.builder()
            .content("hello Paul")
            .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(any(Message.class))).willReturn(response);

        //when
        JpaMessageResponse result = messageService.updateMessage(messageId, request);

        //then
        assertThat(result).isEqualTo(response);
        assertThat(result.content()).isEqualTo("hello Paul");

        // toDto 호출 직전 넘겨주는 값 확인
        // 넘겨주는 값이 중간에 변경되는것을 확인 가능
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        then(messageMapper).should().toDto(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo("hello Paul");

        then(messageRepository).should(times(1)).findById(messageId);
        then(messageMapper).should(times(1)).toDto(any(Message.class));
    }

    @DisplayName("문자를 찾을 수 없을경우 MessageNotFoundException 반환.")
    @Test
    void whenMessageNotFound_thenMessageThrowsMessageNotFoundException() throws Exception {
        //given
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> messageService.updateMessage(messageId, any()))
            .isInstanceOf(MessageNotFoundException.class)
            .extracting("details", map(String.class, Object.class))
            .containsEntry("messageId", messageId);

        //then
        then(messageMapper).shouldHaveNoInteractions();
    }

    @DisplayName("로직에 문제가 없으면 삭제 진행한다.")
    @Test
    void whenLoginIsValid_thenDeleteMessage() {
        // given
        given(messageRepository.existsById(any(UUID.class))).willReturn(true);

        // when
        messageService.deleteMessage(messageId);

        // then
        then(messageRepository).should(times(1)).deleteById(messageId);
    }

    @DisplayName("아이디가 존재하지 않으면 MessageNotFoundException을 반환한다.")
    @Test
    void whenMessageNotExists_thenMessageThrowsMessageNotFoundException() {
        UUID messageId = UUID.randomUUID();
        given(messageRepository.existsById(messageId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> messageService.deleteMessage(messageId))
            .isInstanceOf(MessageNotFoundException.class)
            .hasMessageContaining("메세지를 찾을 수 없습니다.");

        then(messageRepository).should(times(1)).existsById(messageId);
        then(messageRepository).should(never()).deleteById(any());
    }

    @DisplayName("첫 호출시 커서는 가장 최근 메세지를 반환한다.")
    @Test
    void whenFirstTimeFind_(){
        // given
        int numberOfMessages = 5;
        int pageSize = 3;
        List<Message> messages = new ArrayList<>();

        for (int i = numberOfMessages; i > 0; i--) {
            String dateStr = String.format("2025-06-%02dT00:00:00Z", i);
            Instant createdAt = Instant.parse(dateStr);
            Message message = Message.builder().content(dateStr).build();
            ReflectionTestUtils.setField(message, "createdAt", createdAt);
            messages.add(message);
        }

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
        given(messageRepository.findSliceByCursor(channelId, null, pageable))
            .willReturn(messages.subList(0, Math.min(pageSize + 1, messages.size())));

        given(messageMapper.toDto(any(Message.class)))
            .willAnswer(inv -> {
                Message m = inv.getArgument(0);
                return JpaMessageResponse.builder()
                    .content(m.getContent())
                    .createdAt(m.getCreatedAt())
                    .build();
            });

        // when
        AdvancedJpaPageResponse result = messageService.findAllByChannelIdAndCursor(channelId, null, pageable);

        // then
        List<JpaMessageResponse> content = result.content();
        assertThat(content).hasSize(pageSize);

        Instant firstCreatedAt = content.get(0).createdAt();
        Instant maxCreatedAt = content.stream()
            .map(JpaMessageResponse::createdAt)
            .max(Comparator.naturalOrder())
            .orElseThrow();

        assertThat(firstCreatedAt).isEqualTo(maxCreatedAt);

        Instant lastCreatedAt = content.get(content.size() - 1).createdAt();
        assertThat(result.nextCursor()).isEqualTo(lastCreatedAt);

    }

    @DisplayName("다음 페이지가 있을 경우  hasNext는 true를 반환한다.")
    @Test
    void whenPageHasNextPage_hasNextIsTrue() {
        // given
        int numberOfMessages = 5;
        int pageSize = 3;
        List<Message> messages = new ArrayList<>();

        for (int i = numberOfMessages; i > 0; i--) {
            String day = String.format("%02d", i);
            Message message = Message.builder().build();
            ReflectionTestUtils.setField(message, "createdAt", Instant.parse("2025-06-" + day + "T00:00:00Z"));
            messages.add(message);
        }

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
        given(messageRepository.findSliceByCursor(channelId, null, pageable))
            .willReturn(messages.subList(0, pageSize + 1));

        given(messageMapper.toDto(any(Message.class)))
            .willAnswer(inv -> {
                Message m = inv.getArgument(0);
                return JpaMessageResponse.builder()
                    .createdAt(m.getCreatedAt())
                    .build();
            });

        // when
        AdvancedJpaPageResponse result = messageService.findAllByChannelIdAndCursor(channelId, null, pageable);

        // then
        assertThat(result.hasNext()).isTrue();
        assertThat(result.content().size()).isLessThan(numberOfMessages);
    }

}
