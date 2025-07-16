package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService channelService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("공개 채널 생성 성공")
    void create_public_success() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("testPublicChannel",
            "testPublic");

        ChannelDto channelDto = new ChannelDto(
            UUID.randomUUID(), ChannelType.PUBLIC, "testPublicChannel", "testPublic",
            new ArrayList<UserDto>(),
            null
        );

        given(channelRepository.existsById(UUID.randomUUID())).willReturn(false);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.create(request);
        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.description()).isEqualTo(request.description());

        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).should().toDto(any(Channel.class));
    }


    @Test
    @DisplayName("비공개 채널 생성 성공")
    void create_private_success() {
        // given
        List<UUID> participants = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participants);

        ChannelDto channelDto = new ChannelDto(
            UUID.randomUUID(), ChannelType.PRIVATE, null, null,
            new ArrayList<UserDto>(), null
        );

        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);
        given(userRepository.findAllById(participants)).willReturn(
            participants.stream().map(id -> mock(User.class)).toList()
        );

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);

        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).should().toDto(any(Channel.class));
        then(readStatusRepository).should().saveAll(any());
    }


    @Test
    @DisplayName("공개 채널 수정 성공")
    void update_public_channel() {

        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName",
            "newDescription");

        Channel channel = mock(Channel.class);
        given(channel.getType()).willReturn(ChannelType.PUBLIC);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "newName",
            "newDescription", null, null);
        given(channelMapper.toDto(channel)).willReturn(channelDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result.name()).isEqualTo(request.newName());
        assertThat(result.description()).isEqualTo(request.newDescription());

        then(channel).should().update(request.newName(), request.newDescription());
        then(channelMapper).should().toDto(channel);
    }

    @Test
    @DisplayName("비공개 채널 수정 시도시 예외 발생")
    void update_privateChannel_throwsException() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName",
            "newDescription");

        Channel channel = mock(Channel.class);
        given(channel.getType()).willReturn(ChannelType.PRIVATE);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when / then
        assertThrows(PrivateChannelUpdateException.class,
            () -> channelService.update(channelId, request));
    }


    @Test
    @DisplayName("존재하지 않는 채널 수정 시도시 예외 발생")
    void update_channelNotFound_throwsException() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("name", "desc");

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ChannelNotFoundException.class,
            () -> channelService.update(channelId, request));
    }

    @Test
    @DisplayName("채널 삭제 테스트")
    void delete() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(true);

        // when
        channelService.delete(channelId);

        // then
        then(messageRepository).should().deleteAllByChannelId(channelId);
        then(readStatusRepository).should().deleteAllByChannelId(channelId);
        then(channelRepository).should().deleteById(channelId);

        verify(messageRepository).deleteAllByChannelId(channelId);
        verify(readStatusRepository).deleteAllByChannelId(channelId);
        verify(channelRepository).deleteById(channelId);
        
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않음")
    void delete_channelNotFound_throwsException() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(false);

        // when & then
        assertThrows(ChannelNotFoundException.class, () -> channelService.delete(channelId));
    }


    @Test
    @DisplayName("사용자 채널 목록 조회 성공")
    void findAllByUserId_success() {
        // given
        UUID userId = UUID.randomUUID();
        Channel channel1 = mock(Channel.class);
        Channel channel2 = mock(Channel.class);

        ReadStatus readStatus1 = mock(ReadStatus.class);
        ReadStatus readStatus2 = mock(ReadStatus.class);
        given(readStatus1.getChannel()).willReturn(channel1);
        given(readStatus2.getChannel()).willReturn(channel2);

        given(readStatusRepository.findAllByUserId(userId)).willReturn(
            List.of(readStatus1, readStatus2));

        given(channel1.getId()).willReturn(UUID.randomUUID());
        given(channel2.getId()).willReturn(UUID.randomUUID());
        List<UUID> channelIds = List.of(channel1.getId(), channel2.getId());

        Channel publicChannel = mock(Channel.class);
        given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, channelIds))
            .willReturn(List.of(publicChannel));

        ChannelDto dto = mock(ChannelDto.class);
        given(channelMapper.toDto(publicChannel)).willReturn(dto);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(dto);

        verify(channelRepository).findAll();
        verify(readStatusRepository).findAllByUserId(userId);
    }

}