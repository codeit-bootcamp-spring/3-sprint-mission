package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicChannelService 단위 테스트")
public class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService channelService;

    private UUID channelId;
    private UUID userId;
    private String channelName;
    private String channelDescription;
    private Channel publicChannel;
    private Channel privateChannel;
    private ChannelDto channelDto;
    private User user;
    private PublicChannelUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();
        channelName = "testChannel";
        channelDescription = "testDescription";

        user = new User("user1", "test@abc.com", "1234", null);
        publicChannel = new Channel(ChannelType.PUBLIC, channelName, channelDescription);
        ReflectionTestUtils.setField(publicChannel, "id", channelId);
        privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, channelName, channelDescription,
            List.of(), Instant.now());
        updateRequest = new PublicChannelUpdateRequest("이름1", "설명1");
    }

    @Test
    @DisplayName("공개채널을 생성할 수 있다.")
    void createPublicChannel_success() {
        //given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "내용");
        given(channelRepository.save(any(Channel.class))).willReturn(publicChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        //when
        ChannelDto result = channelService.create(request);

        //then
        assertThat(result).isEqualTo(channelDto);
    }

    @Test
    @DisplayName("비공개채널을 생성할 수 있다.")
    void createPrivateChannel_success() {
        //given
        List<UUID> ids = List.of(userId);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(ids);

        given(channelRepository.save(any())).willReturn(privateChannel);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.create(request);

        //then
        assertThat(result).isEqualTo(channelDto);
    }

    @Test
    @DisplayName("유저가 존재하지 않으면 채널을 생성할 수 없다.")
    void createChannel_fail_ifUserNotFound() {
        //given
        List<UUID> ids = List.of(userId);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(ids);
        given(channelRepository.save(any())).willReturn(privateChannel);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> channelService.create(request))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 입력값으로 채널을 수정할 수 있다.")
    void updateChannel_success() {
        //given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
        given(channelRepository.save(any())).willReturn(publicChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        //when
        ChannelDto result = channelService.update(channelId, updateRequest);

        //then
        assertThat(result).isEqualTo(channelDto);
    }

    @Test
    @DisplayName("채널이 존재하지 않는다면 채널을 수정할 수 없다.")
    void updateChannel_fail_ifChannelNotFound() {
        //given
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> channelService.update(channelId, updateRequest))
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("비공개채널은 수정할 수 없다.")
    void updateChannel_fail_ifPrivateChannel() {
        // given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        //when, then
        assertThatThrownBy(() -> channelService.update(channelId, updateRequest))
            .isInstanceOf(PrivateChannelUpdateException.class);
    }

    @Test
    @DisplayName("채널을 삭제할 수 있다.")
    void deleteChannel_success() {
        //given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));

        //when
        channelService.deleteChannel(channelId);

        //then
        then(messageRepository).should().deleteByChannelId(channelId);
        then(readStatusRepository).should().deleteByChannelId(channelId);
        then(channelRepository).should().deleteById(channelId);
    }

    @Test
    @DisplayName("채널이 없다면 채널을 삭제할 수 없다.")
    void deleteChannel_fail_ifChannelNotFound() {
        //given
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> channelService.deleteChannel(channelId))
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("사용자별 채널을 조회할 수 있다.")
    void findChannel_success() {
        //given
        List<ReadStatus> readStatuses = List.of(new ReadStatus(user, publicChannel, Instant.now()));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(readStatusRepository.findAllByUserId(eq(userId))).willReturn(readStatuses);
        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),
            eq(List.of(publicChannel.getId()))))
            .willReturn(List.of(publicChannel));
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        //when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        //then
        assertThat(result).containsExactly(channelDto);
    }

    @Test
    @DisplayName("사용자가 존재하지 않는다면 채널을 조회할 수 없다.")
    void findChannle_fail_ifUserNotFound() {
        //given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> channelService.findAllByUserId(userId))
            .isInstanceOf(UserNotFoundException.class);
    }
}