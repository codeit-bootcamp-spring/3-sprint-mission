package com.sprint.mission.discodeit.service;

import static com.sprint.mission.discodeit.fixture.ChannelFixture.createPrivateChannel;
import static com.sprint.mission.discodeit.fixture.ChannelFixture.createPublicChannel;
import static com.sprint.mission.discodeit.fixture.UserFixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Channel.PrivateChannelUpdateException;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelService 단위 테스트")
public class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService channelService;

    private Instant fixedTime;

    @BeforeEach
    void setUp() {
        fixedTime = Instant.parse("2025-01-01T00:00:00Z");
    }

    @Test
    @DisplayName("유효한 생성 요청으로 공개 채널을 생성할 수 있다.")
    void shouldCreatePublicChannel_whenValidRequest() {

        // given
        String channelName = "공개 채널 테스트";
        String description = "공개 채널 생성 테스트입니다.";
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(channelName,
                description);
        Channel publicCh = createPublicChannel(channelName, description);
        ChannelDto publicChDto = new ChannelDto(publicCh.getId(), publicCh.getName(),
                publicCh.getDescription(),
                publicCh.getType(), null, List.of());

        given(channelRepository.save(any(Channel.class))).willReturn(publicCh);
        given(channelMapper.toDto(any(Channel.class))).willReturn(publicChDto);

        // when
        ChannelDto result = channelService.create(createRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(channelName);
        assertThat(result.description()).isEqualTo(description);
    }

    @Test
    @DisplayName("유효한 생성 요청으로 개인 채널을 생성할 수 있다.")
    void shouldCreatePrivateChannel_whenValidRequest() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
                List.of(user.getId()));
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null,
                true);
        Channel privateCh = createPrivateChannel();
        ChannelDto privateChDto = new ChannelDto(privateCh.getId(), privateCh.getName(),
                privateCh.getDescription(),
                privateCh.getType(), null, List.of(userDto));

        given(channelRepository.save(any(Channel.class))).willReturn(privateCh);
        given(channelMapper.toDto(any(Channel.class))).willReturn(privateChDto);

        // when
        ChannelDto result = channelService.create(createRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isNull();
        assertThat(result.description()).isNull();
        assertThat(result.participants()).containsExactly(userDto);
    }

    @Test
    @DisplayName("유효한 수정 요청으로 공개 채널을 수정할 수 있다.")
    void shouldUpdatePublicChannel_whenValidRequest() {

        // given
        String newChannelName = "공개 채널 수정 테스트 채널";
        String newDescription = "공개 채널 수정 테스트 채널입니다.";
        Channel originalCh = createPublicChannel("테스트 채널", "테스트 채널입니다.");
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(newChannelName,
                newDescription);
        Channel updatedCh = createPublicChannel(newChannelName, newDescription);
        ChannelDto publicChDto = new ChannelDto(updatedCh.getId(), updatedCh.getName(),
                updatedCh.getDescription(), updatedCh.getType(), null, List.of());

        given(channelRepository.findById(originalCh.getId())).willReturn(
                Optional.of(originalCh));
        given(channelRepository.save(any(Channel.class))).willReturn(updatedCh);
        given(channelMapper.toDto(any(Channel.class))).willReturn(publicChDto);

        // when
        ChannelDto result = channelService.update(originalCh.getId(), updateRequest);

        // then
        assertThat(result.name()).isEqualTo(newChannelName);
        assertThat(result.description()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("개인 채널 수정 요청 시 예외가 발생한다.")
    void shouldThrowException_whenUpdatingPrivateChannel() {

        // given
        String newChannelName = "개인 채널 수정 테스트 채널";
        String newDescription = "개인 채널 수정 테스트 채널입니다.";
        Channel originalCh = createPrivateChannel();
        UUID channelId = originalCh.getId();
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(newChannelName,
                newDescription);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(originalCh));

        // when & then
        assertThatThrownBy(() -> {
            channelService.update(channelId, updateRequest);
        }).isInstanceOf(PrivateChannelUpdateException.class);

        then(channelRepository).should().findById(channelId);
    }

    @Test
    @DisplayName("유효한 삭제 요청으로 채널을 삭제할 수 있다.")
    void shouldDeleteChannel_whenValidRequest() {

        // given
        Channel publicCh = createPublicChannel("채널 삭제 테스트", "채널 삭제 테스트입니다.");
        UUID publicChId = publicCh.getId();

        given(channelRepository.existsById(publicChId)).willReturn(true);

        willDoNothing().given(messageRepository).deleteAllByChannelId(publicChId);
        willDoNothing().given(readStatusRepository).deleteAllByChannelId(publicChId);
        willDoNothing().given(channelRepository).deleteById(publicChId);

        // when
        channelService.delete(publicChId);

        // then
        then(channelRepository).should().existsById(publicChId);
        then(messageRepository).should().deleteAllByChannelId(publicChId);
        then(readStatusRepository).should().deleteAllByChannelId(publicChId);
        then(channelRepository).should().deleteById(publicChId);
    }

    @Test
    @DisplayName("존재하지 않는 채널 삭제 시도 시 예외가 발생한다.")
    void shouldThrowException_whenDeletingNonExistentChannel() {

        // given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.existsById(channelId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            channelService.delete(channelId);
        }).isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().existsById(channelId);
        then(messageRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
        then(channelRepository).should(never()).deleteById(any());
    }

    @Test
    @DisplayName("유효한 유저ID로 해당 유저가 조회 가능한 모든 채널을 조회할 수 있다.")
    void shouldFindAllChannelsByUserId_whenValidRequest() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        UUID userId = user.getId();
        Channel publicCh = createPublicChannel("공개 채널", "공개 채널입니다.");
        Channel privateCh = createPrivateChannel();
        ChannelDto publicChannelDto = new ChannelDto(publicCh.getId(), publicCh.getName(),
                publicCh.getDescription(), publicCh.getType(), fixedTime, List.of());
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null,
                true);
        ChannelDto privateChannelDto = new ChannelDto(privateCh.getId(),
                privateCh.getName(), privateCh.getDescription(), privateCh.getType(),
                fixedTime, List.of(userDto));

        given(channelRepository.findAllPublicOrUserChannels(userId)).willReturn(
                List.of(publicCh, privateCh));
        given(channelMapper.toDto(publicCh)).willReturn(publicChannelDto);
        given(channelMapper.toDto(privateCh)).willReturn(privateChannelDto);

        // when
        List<ChannelDto> channels = channelService.findAllByUserId(userId);

        // then
        assertThat(channels).hasSize(2);
        assertThat(channels).containsExactly(publicChannelDto, privateChannelDto);
    }

    @Test
    @DisplayName("존재하지 않은 유저ID로 조회 요청시 공개 채널만 반환한다.")
    void shouldReturnOnlyPublicChannels_whenUserDoesNotExist() {

        // given
        UUID userId = UUID.randomUUID();
        Channel publicCh = createPublicChannel("공개 채널", "공개 채널입니다.");
        ChannelDto publicChannelDto = new ChannelDto(publicCh.getId(), publicCh.getName(),
                publicCh.getDescription(), publicCh.getType(), fixedTime, List.of());

        given(channelRepository.findAllPublicOrUserChannels(userId)).willReturn(List.of(publicCh));
        given(channelMapper.toDto(publicCh)).willReturn(publicChannelDto);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(publicChannelDto);
    }
}
