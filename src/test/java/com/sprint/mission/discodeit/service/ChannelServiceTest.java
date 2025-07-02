package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelModificationException;
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
import org.junit.jupiter.api.Nested;
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
    @DisplayName("테스트 환경 설정 확인")
    void setUp() {
        assertNotNull(readStatusRepository);
        assertNotNull(messageRepository);
        assertNotNull(userRepository);
        assertNotNull(channelMapper);
        assertNotNull(channelService);
    }

    @Nested
    @DisplayName("채널 생성 테스트")
    class ChannelCreateTests {

        @Test
        @DisplayName("공개 채널 생성 성공")
        void createPublicChannel_Success() {
            // Given
            String channelName = "testchannel";
            String description = "test description";

            PublicChannelCreateRequest request = new PublicChannelCreateRequest(channelName,
                description);
            Channel savedChannel = new Channel(ChannelType.PUBLIC, channelName, description);
            ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC,
                channelName, description, null, Instant.now());

            given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
            given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);
            // When
            ChannelDto result = channelService.create(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(channelName);
            assertThat(result.description()).isEqualTo(description);
            assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
            assertThat(result.participants()).isNull();

            then(channelRepository).should().save(any(Channel.class));
        }

        @Test
        @DisplayName("프라이빗 채널 생성 완료")
        void createPrivateChannel_Success() {
            // Given
            List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());

            User user1 = new User("user1", "user1@example.com", "pass", null);
            User user2 = new User("user2", "user2@example.com", "pass", null);
            List<User> participants = List.of(user1, user2);

            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
            Channel savedChannel = new Channel(ChannelType.PRIVATE, null, null);
            ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null,
                null, null, Instant.now());

            given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
            given(userRepository.findAllById(participantIds)).willReturn(participants);
            given(readStatusRepository.saveAll(anyList())).willReturn(List.of());
            given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

            // When
            ChannelDto result = channelService.create(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
            assertThat(result.name()).isNull();
            assertThat(result.description()).isNull();

            then(channelRepository).should().save(any(Channel.class));
            then(userRepository).should().findAllById(participantIds);
            then(readStatusRepository).should().saveAll(anyList());
            then(channelMapper).should().toDto(any(Channel.class));
        }
    }

    @Test
    @DisplayName("사용자별 채널 목록 조회 성공")
    void findAllByUserId_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID channelId1 = UUID.randomUUID();
        UUID channelId2 = UUID.randomUUID();

        Channel channel1 = mock(Channel.class);
        Channel channel2 = mock(Channel.class);

        ReadStatus readStatus1 = new ReadStatus(null, channel1, Instant.now());
        ReadStatus readStatus2 = new ReadStatus(null, channel2, Instant.now());

        ChannelDto dto1 = mock(ChannelDto.class);
        ChannelDto dto2 = mock(ChannelDto.class);

        given(channel1.getId()).willReturn(channelId1);
        given(channel2.getId()).willReturn(channelId2);
        given(readStatusRepository.findAllByUserId(userId))
            .willReturn(List.of(readStatus1, readStatus2));
        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),
            eq(List.of(channelId1, channelId2))
        ))
            .willReturn(List.of(channel1, channel2));
        given(channelMapper.toDto(channel1)).willReturn(dto1);
        given(channelMapper.toDto(channel2)).willReturn(dto2);

        // When
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // Then
        assertThat(result).containsExactly(dto1, dto2);
        then(readStatusRepository).should().findAllByUserId(userId);
        then(channelRepository).should()
            .findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), eq(List.of(channelId1, channelId2)));
        then(channelMapper).should().toDto(channel1);
        then(channelMapper).should().toDto(channel2);

    }

    @Nested
    @DisplayName("채널 수정 테스트")
    class ChannelUpdateTests {

        @Test
        @DisplayName("공개 채널 수정 성공")
        void updatePublicChannel_Success() {
            //Given
            UUID channelId = UUID.randomUUID();
            String newChannelName = "testchannel";
            String newDescription = "test description";

            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest(newChannelName, newDescription);
            Channel existingChannel = new Channel(ChannelType.PUBLIC, "oldchannel",
                "old description");
            ChannelDto expectedDto = new ChannelDto(channelId, ChannelType.PUBLIC, newChannelName,
                newDescription, null, Instant.now());

            given(channelRepository.findById(channelId)).willReturn(
                Optional.of(existingChannel));
            given(channelMapper.toDto(existingChannel)).willReturn(expectedDto);

            // When
            ChannelDto result = channelService.update(channelId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(newChannelName);
            assertThat(result.description()).isEqualTo(newDescription);

            then(channelRepository).should().findById(channelId);
            then(channelMapper).should().toDto(existingChannel);

        }

        @Test
        @DisplayName("채널 수정 실패 - 채널 없음")
        void updateChannel_Fail_ChannelNotFound() {
            //Given
            UUID channelId = UUID.randomUUID();

            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("newchannel", "new description");

            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(
                () -> channelService.update(channelId, request)
            ).isInstanceOf(ChannelNotFoundException.class);

            then(channelRepository).should().findById(channelId);
            then(channelMapper).shouldHaveNoInteractions();

        }

        @Test
        @DisplayName("채널 수정 실패 - 프라이빗 채널 수정 시도")
        void updateChannel_Fail_PrivateChannelModification() {
            // Given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("newchannel", "new description");

            Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

            // When & Then
            assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(PrivateChannelModificationException.class);

            then(channelRepository).should().findById(channelId);
            then(channelMapper).shouldHaveNoInteractions();

        }
    }

    @Nested
    @DisplayName("채널 삭제 테스트")
    class ChannelDeleteTests {

        @Test
        @DisplayName("채널 삭제 성공")
        void deleteChannel_Success() {
            // Given
            UUID channelId = UUID.randomUUID();

            given(channelRepository.existsById(channelId)).willReturn(true);

            // When
            channelService.delete(channelId);

            // Then
            then(channelRepository).should().existsById(channelId);
            then(messageRepository).should().deleteAllByChannelId(channelId);
            then(readStatusRepository).should().deleteAllByChannelId(channelId);
            then(channelRepository).should().deleteById(channelId);
        }

        @Test
        @DisplayName("채널 삭제 실패 - 채널 없음")
        void deleteChannel_Fail_ChannelNotFound() {
            // Given
            UUID channelId = UUID.randomUUID();

            given(channelRepository.existsById(channelId)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> channelService.delete(channelId))
                .isInstanceOf(ChannelNotFoundException.class);

            then(channelRepository).should().existsById(channelId);
            then(channelRepository).shouldHaveNoMoreInteractions();
            then(messageRepository).shouldHaveNoInteractions();
            then(readStatusRepository).shouldHaveNoInteractions();
        }

    }
}
