package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
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
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * ChannelService의 주요 기능(공개/비공개 채널 생성, 수정, 삭제 등)에 대한 단위 테스트 클래스입니다.
 * <p>
 * - 채널 생성, 수정, 삭제, 예외 상황 등을 검증합니다.
 * - Mockito를 활용한 Mock 객체 기반 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelService 단위 테스트")
public class ChannelServiceTest {

    @InjectMocks
    private BasicChannelService basicChannelService;

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

    private UUID channelId;
    private UUID userId;
    private Channel mockChannel;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();
        mockChannel = new Channel(ChannelType.PUBLIC, "test", "desc");
        mockChannel.setId(channelId);
    }

    @Nested
    @DisplayName("공개 채널 생성 테스트")
    class CreatePublicChannelTests {

        /**
         * [성공] 공개 채널을 정상적으로 생성하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 공개 채널 생성")
        void shouldCreatePublicChannelSuccessfully() {

            // Given
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("name", "desc");
            given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
            given(channelMapper.toDto(any(Channel.class))).willReturn(mockChannelDto());

            // When
            ChannelDto result = basicChannelService.create(request);

            // Then
            assertThat(result).isNotNull();
            then(channelRepository).should().save(any(Channel.class));
        }

        /**
         * [실패] DB 제약조건 위반 시 예외가 발생하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 공개 채널 생성 - DB 제약조건 위반")
        void shouldFailToCreatePublicChannel_WhenDBConstraintViolation() {

            // Given
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("name", "desc");
            given(channelRepository.save(any(Channel.class)))
                    .willThrow(new DataIntegrityViolationException("DB 제약조건 위반"));

            // When & Then
            assertThatThrownBy(() -> basicChannelService.create(request))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessage("DB 제약조건 위반");
        }
    }

    @Nested
    @DisplayName("비공개 채널 생성 테스트")
    class CreatePrivateChannelTests {

        /**
         * [성공] 비공개 채널을 정상적으로 생성하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 비공개 채널 생성")
        void shouldCreatePrivateChannelSuccessfully() {
            // Given
            List<UUID> participantIds = List.of(userId);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
            given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
            given(userRepository.findById(userId)).willReturn(Optional.of(new User("u", "e", "p", null)));
            given(readStatusRepository.save(any(ReadStatus.class)))
                    .willAnswer(inv -> inv.getArgument(0));
            given(channelMapper.toDto(any(Channel.class))).willReturn(mockChannelDto());

            // When
            ChannelDto result = basicChannelService.create(request);

            // Then
            assertThat(result).isNotNull();
            then(channelRepository).should().save(any(Channel.class));
            then(readStatusRepository).should().save(any(ReadStatus.class));
        }

        /**
         * [성공] 비공개 채널 생성 시 참여자가 존재하지 않을 때의 동작을 검증합니다.
         */
        @Test
        @DisplayName("[성공] 비공개 채널 생성 - 참여자 없음")
        void shouldHandleMissingParticipantsGracefully() {
            // Given
            List<UUID> participantIds = List.of(userId);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
            given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
            given(userRepository.findById(userId)).willReturn(Optional.empty());
            given(channelMapper.toDto(any(Channel.class))).willReturn(mockChannelDto());

            // When
            ChannelDto result = basicChannelService.create(request);

            // Then
            assertThat(result).isNotNull();
            then(readStatusRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("공개 채널 수정 테스트")
    class UpdatePublicChannelTests {

        @Test
        @DisplayName("공개 채널 수정 성공")
        void shouldUpdatePublicChannelSuccessfully() {
            // Given
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "desc");
            given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
            given(channelMapper.toDto(any(Channel.class))).willReturn(mockChannelDto());

            // When
            ChannelDto result = basicChannelService.update(channelId, request);

            // Then
            assertThat(result).isNotNull();
            then(channelRepository).should().save(mockChannel);
        }

        @Test
        @DisplayName("공개 채널 수정 실패 - 채널이 존재하지 않음")
        void shouldFailToUpdate_WhenChannelNotFound() {
            // Given
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "desc");

            // When & Then
            assertThatThrownBy(() -> basicChannelService.update(channelId, request))
                    .isInstanceOf(ChannelNotFoundException.class);
        }

        @Test
        @DisplayName("공개 채널 수정 실패 - 비공개 채널임")
        void shouldFailToUpdate_WhenChannelIsPrivate() {
            // Given
            mockChannel = new Channel(ChannelType.PRIVATE, null, null);
            mockChannel.setId(channelId);
            given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "desc");

            // When & Then
            assertThatThrownBy(() -> basicChannelService.update(channelId, request))
                    .isInstanceOf(PrivateChannelUpdateException.class);
        }
    }

    @Nested
    @DisplayName("채널 삭제 테스트")
    class DeleteChannelTests {

        @Test
        @DisplayName("채널 삭제 성공")
        void shouldDeleteChannelSuccessfully() {
            // Given
            given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

            // When
            basicChannelService.delete(channelId);

            // Then
            then(messageRepository).should().deleteAllByChannelId(channelId);
            then(readStatusRepository).should().deleteAllByChannelId(channelId);
            then(channelRepository).should().deleteById(channelId);
        }

        @Test
        @DisplayName("채널 삭제 실패 - 채널이 존재하지 않음")
        void shouldFailToDelete_WhenChannelNotFound() {
            // Given
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicChannelService.delete(channelId))
                    .isInstanceOf(ChannelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("유저 ID 대응 채널 탐색 테스트")
    class FindChannelsByUserIdTests {

        @Test
        @DisplayName("유저가 접근 가능한 채널 조회 성공")
        void shouldReturnAccessibleChannels() {
            // Given
            Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
            privateChannel.setId(UUID.randomUUID());

            given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(new ReadStatus(null, privateChannel, Instant.now())));
            given(channelRepository.findAll()).willReturn(List.of(mockChannel, privateChannel));
            given(channelMapper.toDto(any(Channel.class))).willReturn(mockChannelDto());

            // When
            List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

            // Then
            assertThat(result).isNotNull();
            then(channelRepository).should().findAll();
        }
    }

    // === Helper ===
    private ChannelDto mockChannelDto() {
        return new ChannelDto(
                channelId,
                ChannelType.PUBLIC,
                "name",
                "desc",
                List.of(),
                Instant.now()
        );
    }
}

