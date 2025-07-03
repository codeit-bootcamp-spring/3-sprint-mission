package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.testutil.TestConstants.NON_EXISTENT_CHANNEL_ID;
import static com.sprint.mission.discodeit.testutil.TestConstants.NON_EXISTENT_USER_ID;
import static com.sprint.mission.discodeit.testutil.TestConstants.TEST_CHANNEL_DESCRIPTION;
import static com.sprint.mission.discodeit.testutil.TestConstants.TEST_CHANNEL_NAME;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.CHANNEL_ID_1;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.CHANNEL_ID_2;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.USER_ID_1;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createDefaultUser;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createPrivateChannel;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createPrivateChannelCreateRequest;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createPublicChannel;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createPublicChannelCreateRequest;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createPublicChannelDto;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createPublicChannelUpdateRequest;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createUserList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateParticipantsException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicChannelService 단위 테스트")
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private MapperFacade mapperFacade;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublicChannel_Success() {
    // Given
    PublicChannelCreateRequest request = createPublicChannelCreateRequest();
    Channel savedChannel = createPublicChannel();

    given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

    // When
    Channel result = channelService.create(request);

    // Then
    assertThat(result).isEqualTo(savedChannel);
    assertThat(result.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(result.getName()).isEqualTo(TEST_CHANNEL_NAME);
    assertThat(result.getDescription()).isEqualTo(TEST_CHANNEL_DESCRIPTION);
    then(channelRepository).should().save(any(Channel.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivateChannel_Success() {
    // Given
    List<User> participants = createUserList();
    PrivateChannelCreateRequest request = createPrivateChannelCreateRequest();
    Channel savedChannel = createPrivateChannel();

    given(userRepository.findAllById(request.participantIds())).willReturn(participants);
    given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

    // When
    Channel result = channelService.create(request);

    // Then
    assertThat(result).isEqualTo(savedChannel);
    assertThat(result.getType()).isEqualTo(ChannelType.PRIVATE);
    then(channelRepository).should().save(any(Channel.class));
    then(userRepository).should().findAllById(request.participantIds());
    then(readStatusRepository).should().saveAll(anyList());
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - 존재하지 않는 참가자")
  void createPrivateChannel_Fail_UserNotFound() {
    // Given
    PrivateChannelCreateRequest request = createPrivateChannelCreateRequest();
    Channel savedChannel = createPrivateChannel();
    List<User> incompleteParticipants = Collections.singletonList(createDefaultUser()); // 1명만 조회됨

    given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
    given(userRepository.findAllById(request.participantIds())).willReturn(incompleteParticipants);

    // When & Then
    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(UserNotFoundException.class);

    then(channelRepository).should().save(any(Channel.class));
    then(channelRepository).should().delete(savedChannel); // 롤백
    then(userRepository).should().findAllById(request.participantIds());
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - 중복된 참가자 ID")
  void createPrivateChannel_Fail_DuplicateParticipants() {
    // Given
    List<java.util.UUID> duplicateIds = Arrays.asList(USER_ID_1, USER_ID_1); // 중복
    PrivateChannelCreateRequest request = createPrivateChannelCreateRequest(duplicateIds);

    // When & Then
    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(DuplicateParticipantsException.class);

    then(channelRepository).should(never()).save(any(Channel.class));
  }

  @Test
  @DisplayName("공개 채널 수정 성공")
  void updatePublicChannel_Success() {
    // Given
    Channel existingChannel = createPublicChannel();
    PublicChannelUpdateRequest request = createPublicChannelUpdateRequest();

    given(channelRepository.findById(CHANNEL_ID_1)).willReturn(Optional.of(existingChannel));

    // When
    Channel result = channelService.update(CHANNEL_ID_1, request);

    // Then
    assertThat(result).isEqualTo(existingChannel);
    then(channelRepository).should().findById(CHANNEL_ID_1);
  }

  @Test
  @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
  void updateChannel_Fail_ChannelNotFound() {
    // Given
    PublicChannelUpdateRequest request = createPublicChannelUpdateRequest();
    given(channelRepository.findById(NON_EXISTENT_CHANNEL_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> channelService.update(NON_EXISTENT_CHANNEL_ID, request))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should().findById(NON_EXISTENT_CHANNEL_ID);
  }

  @Test
  @DisplayName("채널 수정 실패 - 비공개 채널 수정 시도")
  void updateChannel_Fail_PrivateChannelUpdate() {
    // Given
    Channel privateChannel = createPrivateChannel();
    PublicChannelUpdateRequest request = createPublicChannelUpdateRequest();

    given(channelRepository.findById(CHANNEL_ID_2)).willReturn(Optional.of(privateChannel));

    // When & Then
    assertThatThrownBy(() -> channelService.update(CHANNEL_ID_2, request))
        .isInstanceOf(PrivateChannelUpdateException.class);

    then(channelRepository).should().findById(CHANNEL_ID_2);
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_Success() {
    // Given
    Channel existingChannel = createPublicChannel();
    given(channelRepository.findById(CHANNEL_ID_1)).willReturn(Optional.of(existingChannel));

    // When
    channelService.delete(CHANNEL_ID_1);

    // Then
    then(channelRepository).should().findById(CHANNEL_ID_1);
    then(channelRepository).should().delete(existingChannel);
  }

  @Test
  @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
  void deleteChannel_Fail_ChannelNotFound() {
    // Given
    given(channelRepository.findById(NON_EXISTENT_CHANNEL_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> channelService.delete(NON_EXISTENT_CHANNEL_ID))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should().findById(NON_EXISTENT_CHANNEL_ID);
    then(channelRepository).should(never()).delete(any(Channel.class));
  }

  @Test
  @DisplayName("채널 조회 성공")
  void findChannel_Success() {
    // Given
    Channel existingChannel = createPublicChannel();
    ChannelDto expectedDto = createPublicChannelDto();

    given(channelRepository.findByIdWithParticipants(CHANNEL_ID_1)).willReturn(Optional.of(existingChannel));
    given(mapperFacade.toDto(existingChannel)).willReturn(expectedDto);

    // When
    ChannelDto result = channelService.find(CHANNEL_ID_1);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(channelRepository).should().findByIdWithParticipants(CHANNEL_ID_1);
    then(mapperFacade).should().toDto(existingChannel);
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    // Given
    given(userRepository.existsById(USER_ID_1)).willReturn(true);
    given(readStatusRepository.findChannelIdsByUserId(USER_ID_1)).willReturn(Collections.emptyList());
    given(channelRepository.findPublicChannelsWithParticipants()).willReturn(Collections.emptyList());

    // When
    List<ChannelDto> result = channelService.findAllByUserId(USER_ID_1);

    // Then
    then(userRepository).should().existsById(USER_ID_1);
    then(readStatusRepository).should().findChannelIdsByUserId(USER_ID_1);
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 실패 - 존재하지 않는 사용자")
  void findAllByUserId_Fail_UserNotFound() {
    // Given
    given(userRepository.existsById(NON_EXISTENT_USER_ID)).willReturn(false);

    // When & Then
    assertThatThrownBy(() -> channelService.findAllByUserId(NON_EXISTENT_USER_ID))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should().existsById(NON_EXISTENT_USER_ID);
    then(readStatusRepository).should(never()).findChannelIdsByUserId(any());
  }
}