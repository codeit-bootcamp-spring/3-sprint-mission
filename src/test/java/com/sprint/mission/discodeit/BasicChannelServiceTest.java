package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicChannelServiceTest {

  private ChannelRepository channelRepository;
  private ReadStatusRepository readStatusRepository;
  private MessageRepository messageRepository;
  private UserRepository userRepository;
  private ChannelMapper channelMapper;
  private BasicChannelService channelService;

  @BeforeEach
  void setUp() {
    channelRepository = mock(ChannelRepository.class);
    readStatusRepository = mock(ReadStatusRepository.class);
    messageRepository = mock(MessageRepository.class);
    userRepository = mock(UserRepository.class);
    channelMapper = mock(ChannelMapper.class);

    channelService = new BasicChannelService(
        channelRepository,
        readStatusRepository,
        messageRepository,
        userRepository,
        channelMapper
    );
  }

  @Test
  void createPublicChannel_shouldReturnSavedChannel() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "description");
    Channel saved = new Channel(ChannelType.PUBLIC, "general", "description");
    ChannelResponse response = new ChannelResponse(
        saved.getId(), saved.getType(), saved.getName(), saved.getDescription(),
        Instant.now(), List.of()
    );

    when(channelRepository.save(any())).thenReturn(saved);
    when(channelMapper.toResponse(saved)).thenReturn(response);

    ChannelResponse result = channelService.createPublicChannel(request);

    assertEquals("general", result.name());
    assertEquals(ChannelType.PUBLIC, result.type());
  }

  @Test
  void createPrivateChannel_shouldReturnChannelWithParticipants() {
    UUID userId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    ChannelResponse response = new ChannelResponse(
        channel.getId(), channel.getType(), null, null, Instant.now(), List.of(userId)
    );

    User user = mock(User.class);
    when(user.getId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(channelRepository.save(any())).thenReturn(channel);
    when(channelMapper.toResponse(channel)).thenReturn(response);

    ChannelResponse result = channelService.createPrivateChannel(request);

    assertEquals(ChannelType.PRIVATE, result.type());
  }

  @Test
  void find_existingChannel_shouldReturnDto() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
    ChannelDto dto = new ChannelDto(channel.getId(), channel.getType(), "name", "desc", List.of(),
        Instant.now());

    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));
    when(channelMapper.toDto(channel)).thenReturn(dto);

    ChannelDto result = channelService.find(id);
    assertEquals("name", result.name());
  }

  @Test
  void find_nonExistingChannel_shouldThrow() {
    UUID id = UUID.randomUUID();
    when(channelRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> channelService.find(id));
  }

  @Test
  void findAllByUserId_shouldReturnCorrectChannels() {
    UUID userId = UUID.randomUUID();

    Channel publicChannel = new Channel(ChannelType.PUBLIC, "pub", "desc");
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

    User user = mock(User.class);
    when(user.getId()).thenReturn(userId);

    ReadStatus readStatus = new ReadStatus(user, privateChannel, Instant.now());

    when(readStatusRepository.findAllByUser_Id(userId)).thenReturn(List.of(readStatus));
    when(channelRepository.findAll()).thenReturn(List.of(publicChannel, privateChannel));

    when(channelMapper.toDto(publicChannel)).thenReturn(
        new ChannelDto(publicChannel.getId(), publicChannel.getType(), "pub", "desc", List.of(),
            Instant.now()));
    when(channelMapper.toDto(privateChannel)).thenReturn(
        new ChannelDto(privateChannel.getId(), privateChannel.getType(), null, null, List.of(),
            Instant.now()));

    List<ChannelDto> result = channelService.findAllByUserId(userId);
    assertEquals(2, result.size());
  }

  @Test
  void update_publicChannel_shouldUpdate() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "old", "desc");
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "updated");

    ChannelResponse response = new ChannelResponse(channel.getId(), channel.getType(), "new",
        "updated", Instant.now(), List.of());

    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));
    when(channelMapper.toResponse(channel)).thenReturn(response);

    ChannelResponse result = channelService.update(id, request);

    assertEquals("new", result.name());
  }

  @Test
  void update_privateChannel_shouldThrow() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("name", "desc");

    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));

    assertThrows(IllegalArgumentException.class, () -> channelService.update(id, request));
  }

  @Test
  void delete_existingChannel_shouldDeleteAndReturnResponse() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
    ChannelResponse response = new ChannelResponse(channel.getId(), channel.getType(), "name",
        "desc", Instant.now(), List.of());

    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));
    when(channelMapper.toResponse(channel)).thenReturn(response);

    ChannelResponse result = channelService.delete(id);

    verify(messageRepository).deleteAllByChannel_Id(any(UUID.class));
    verify(readStatusRepository).deleteAllByChannel_Id(any(UUID.class));
    verify(channelRepository).delete(channel);

    assertEquals("name", result.name());
  }

  @Test
  void delete_nonExistingChannel_shouldThrow() {
    UUID id = UUID.randomUUID();
    when(channelRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> channelService.delete(id));
  }
}
