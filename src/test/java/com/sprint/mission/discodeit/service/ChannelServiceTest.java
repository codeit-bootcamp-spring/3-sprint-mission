package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channelException.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channelException.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.userException.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * PackageName  : com.sprint.mission.discodeit.service
 * FileName     : ChannelServiceTest
 * Author       : dounguk
 * Date         : 2025. 6. 19.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Channel unit 테스트")
public class ChannelServiceTest {
    @InjectMocks
    private BasicChannelService channelService;

    @Mock
    private JpaChannelRepository channelRepository;

    @Mock
    private JpaMessageRepository messageRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JpaReadStatusRepository readStatusRepository;

    private Channel channel;

    @DisplayName("public 체널 정상 생성시 올바른 비즈니스 로직이 수행되어야 한다.")
    @Test
    void whenCreateChannelSuccess_thenServiceShouldUseChannelMapperAndRepository() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("name", "description");

        given(channelRepository.save(any(Channel.class)))
            .willAnswer(inv -> inv.getArgument(0));

        ChannelResponse response = ChannelResponse.builder()
            .id(channelId)
            .name("name")
            .description("description")
            .build();

        given(channelMapper.toDto(any(Channel.class)))
            .willReturn(response);

        // when
        ChannelResponse result = channelService.createChannel(request);

        // then
        then(channelRepository).should(times(1)).save(any());
        then(channelMapper).should(times(1)).toDto(any());
        assertThat(result.getName()).isEqualTo("name");
        assertThat(result.getDescription()).isEqualTo("description");
    }


    @DisplayName("private channel 생성시 유저 수가 2명 미만일 경우 UserNotFoundException을 반환한다.")
    @Test
    void whenUsersAreLessThanTwo_thenReturnUserNotFoundException() {
        // given
        Set<UUID> participantIds = new HashSet<>();
        UUID userId1 = UUID.randomUUID();
        participantIds.add(userId1);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        given(userRepository.findAllById(participantIds)).willReturn(Collections.emptyList());


        // when n then
        assertThatThrownBy(() -> channelService.createChannel(request))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should(times(1)).findAllById(participantIds);
        then(readStatusRepository).should(never()).save(any());
        then(channelMapper).should(never()).toDto(any());
        then(channelRepository).should(never()).save(any());
    }

    @DisplayName("사람 수가 2명 이상일 경우 사람 수 만큼의 readStatus가 만들어져야 한다.")
    @Test
    void whenUsersAreMoreThanTwo_thenCreatePrivateChannel() {
        // given
        int numberOfUsers = 2;
        Set<UUID> participantIds = new HashSet<>();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < numberOfUsers; i++) {
            UUID userId = UUID.randomUUID();
            participantIds.add(userId);
            User user = new User();
            users.add(user);
        }

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        given(userRepository.findAllById(participantIds)).willReturn(users);
        given(channelMapper.toDto(any())).willReturn(mock(ChannelResponse.class));

        // when
        ChannelResponse response = channelService.createChannel(request);

        // then
        then(channelRepository).should(times(1)).save(any());
        then(readStatusRepository).should(times(1)).saveAll(any());
        then(channelMapper).should(times(1)).toDto(any());
        assertNotNull(response);
    }

    @Test
    @DisplayName("채널 찾지 못할 경우 ChannelNotFoundException 반환")
    void whenChannelNotExists_thenThrowsChannelNotFoundException() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        given(channelRepository.findById(id)).willReturn(Optional.empty());
        ChannelUpdateRequest request = new ChannelUpdateRequest("name", "description");

        // when
        assertThatThrownBy(()-> channelService.update(id, request))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelMapper).shouldHaveNoMoreInteractions();
    }


    @DisplayName("프라이빗 채널 수정을 시도할 경우 PrivateChannelUpdateException 반환")
    @Test
    void whenChannelIsPrivate_thenShouldNotUpdate() {
        // given
        UUID channelId = UUID.randomUUID();

        ChannelUpdateRequest request = new ChannelUpdateRequest("daniel's channel", "new description");

        channel = Channel.builder()
            .type(ChannelType.PRIVATE)
            .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when
        assertThatThrownBy(() -> channelService.update(channelId, request))
            .isInstanceOf(PrivateChannelUpdateException.class);

        then(channelMapper).shouldHaveNoMoreInteractions();
    }

    @DisplayName("public 채널 수정을 시도할 경우 수정이 되어야 한다.")
    @Test
    void whenChannelIsPublic_thenShouldUpdate() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequest request = new ChannelUpdateRequest("daniel's channel", "new description");
        channel = Channel.builder()
            .type(ChannelType.PUBLIC)
            .build();
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when
        channelService.update(channelId, request);

        // then
        then(channelMapper).should(times(1)).toDto(any());
        assertThat(channel.getName()).isEqualTo(request.newName());
        assertThat(channel.getDescription()).isEqualTo(request.newDescription());
    }



    @Test
    @DisplayName("삭제할 채널이 없을경우 NoSuchElementException을 반환한다.")
    void whenNoChannelToDelete_thenThrowsNoSuchElementException() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(false);

        // when
        assertThatThrownBy(()-> channelService.deleteChannel(channelId))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should(times(1)).existsById(channelId);
        then(channelMapper).shouldHaveNoMoreInteractions();
        then(messageRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
    }

    @DisplayName("정상 로직에선 채널이 삭제 되어야 한다.")
    @Test
    void whenLogicHasNoIssues_thenDeleteChannel() {
        //given
        UUID channelId = UUID.randomUUID();
        channel = Channel.builder()
            .build();

        given(channelRepository.existsById(channelId)).willReturn(true);
        given(readStatusRepository.findAllByChannelId(channelId)).willReturn(any());
        given(messageRepository.findAllByChannelId(channelId)).willReturn(any());

        //when
        channelService.deleteChannel(channelId);

        //then
        then(channelRepository).should(times(1)).deleteById(channelId);
    }

    @DisplayName("채널을 삭제하면 관련 readStatus, message도 삭제 되어야 한다.")
    @Test
    void whenDeleteChannel_thenShouldReadStatusAndMessages() {
        //given
        UUID channelId = UUID.randomUUID();
        channel = Channel.builder().build();

        List<ReadStatus> targetReadStatuses = new ArrayList<>();
        List<Message> targetMessages = new ArrayList<>();

        given(channelRepository.existsById(channelId)).willReturn(true);
        given(readStatusRepository.findAllByChannelId(channelId)).willReturn(targetReadStatuses);
        given(messageRepository.findAllByChannelId(channelId)).willReturn(targetMessages);

        //when
        channelService.deleteChannel(channelId);

        //then
        then(channelRepository).should(times(1)).deleteById(channelId);
        then(readStatusRepository).should(times(1)).deleteAll(targetReadStatuses);
        then(messageRepository).should(times(1)).deleteAll(targetMessages);
    }

    @Test
    @DisplayName("유저 정보가 없을경우 빈 리스트를 반환한다.")
    void whenNoUser_thenReturnEmptyList() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        given(userRepository.existsById(id)).willReturn(false);

        // when
        List<ChannelResponse> result = channelService.findAllByUserId(id);

        // then
        assertThat(result).isEmpty();
        then(channelRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
        then(channelMapper).shouldHaveNoInteractions();
    }


    @DisplayName("정상적인 로직에선 채널을 찾아야 한다.")
    @Test
    void whenLogicHasAnyProblem_thenFindChannels() {
        // given
        UUID userId = UUID.randomUUID();
        UUID publicChannelId = UUID.randomUUID();
        UUID privateChannelId = UUID.randomUUID();

        List<Channel> pulicList = new ArrayList<>();
        Channel publicChannel = Channel.builder().type(ChannelType.PUBLIC).build();
        ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);
        pulicList.add(publicChannel);
        ChannelResponse publicResponse = ChannelResponse.builder()
            .id(publicChannelId).build();

        Channel privateChannel = Channel.builder().type(ChannelType.PRIVATE).build();
        ReflectionTestUtils.setField(privateChannel, "id", privateChannelId);
        ChannelResponse privateResponse = ChannelResponse.builder()
            .id(privateChannelId).build();

        List<ReadStatus> readStatusList = new ArrayList<>();
        ReadStatus readStatus = ReadStatus.builder()
            .channel(privateChannel).build();
        readStatusList.add(readStatus);

        given(userRepository.existsById(userId)).willReturn(true);
        given(channelRepository.findAllByType(ChannelType.PUBLIC)).willReturn(pulicList);
        given(readStatusRepository.findAllByUserIdWithChannel(userId)).willReturn(readStatusList);
        given(channelMapper.toDto(publicChannel)).willReturn(publicResponse);
        given(channelMapper.toDto(privateChannel)).willReturn(privateResponse);

        // when
        List<ChannelResponse> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result.size() == 2).isTrue();
        assertThat(result.get(0).getId()).isEqualTo(publicChannelId);
        assertThat(result.get(1).getId()).isEqualTo(privateChannelId);
    }

    @DisplayName("유저 정보가 없을경우 빈 리스트를 반환한다.")
    @Test
    void whenUserNotExists_thenReturnEmptyList() {
        // given
        given(userRepository.existsById(any())).willReturn(false);

        // when
        channelService.findAllByUserId(any());

        // then
        then(userRepository).should(times(1)).existsById(any());
        then(channelMapper).shouldHaveNoMoreInteractions();
        then(readStatusRepository).shouldHaveNoMoreInteractions();
        then(channelRepository).shouldHaveNoMoreInteractions();
    }
}
