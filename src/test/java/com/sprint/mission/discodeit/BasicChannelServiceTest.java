package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
  private BasicChannelService channelService;

  @BeforeEach
  void setUp() {
    channelRepository = mock(ChannelRepository.class);
    readStatusRepository = mock(ReadStatusRepository.class);
    messageRepository = mock(MessageRepository.class);
    channelService = new BasicChannelService(channelRepository, readStatusRepository,
        messageRepository);
  }

  @Test
  void createPublicChannel_shouldReturnSavedChannel() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "description");
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "description");
    Channel saved = new Channel(ChannelType.PUBLIC, "general", "description");
    when(channelRepository.save(any())).thenReturn(saved);

    ChannelResponse response = channelService.createPublicChannel(request);

    assertEquals("general", response.name());
    assertEquals(ChannelType.PUBLIC, response.type());
  }

  @Test
  void createPrivateChannel_shouldReturnChannelWithParticipants() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    when(channelRepository.save(any())).thenReturn(channel);

    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
    ChannelResponse response = channelService.createPrivateChannel(request);

    assertEquals(ChannelType.PRIVATE, response.type());
  }

  @Test
  void find_existingChannel_shouldReturnDto() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));
    when(messageRepository.findAllByChannelId(any())).thenReturn(List.of());

    assertNotNull(channelService.find(id));
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

    when(readStatusRepository.findAllByUserId(userId)).thenReturn(
        List.of(new ReadStatus(userId, privateChannel.getId(), Instant.now())));
    when(channelRepository.findAll()).thenReturn(List.of(publicChannel, privateChannel));
    when(messageRepository.findAllByChannelId(any())).thenReturn(List.of());
    when(readStatusRepository.findAllByChannelId(any())).thenReturn(List.of());

    List<?> result = channelService.findAllByUserId(userId);
    assertEquals(2, result.size());
  }

  @Test
  void update_publicChannel_shouldUpdate() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "old", "desc");
    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));
    when(channelRepository.save(any())).thenReturn(channel);

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "updated");
    ChannelResponse response = channelService.update(id, request);

    assertEquals("new", response.name());
  }

  @Test
  void update_privateChannel_shouldThrow() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("name", "desc");
    assertThrows(IllegalArgumentException.class, () -> channelService.update(id, request));
  }

  @Test
  void delete_existingChannel_shouldDeleteAndReturnResponse() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));
    when(messageRepository.findAllByChannelId(any())).thenReturn(List.of());

    ChannelResponse response = channelService.delete(id);
    verify(messageRepository).deleteAllByChannelId(any(UUID.class));
    verify(readStatusRepository).deleteAllByChannelId(any(UUID.class));
    verify(channelRepository).deleteById(any(UUID.class));

    assertEquals("name", response.name());
  }

  @Test
  void delete_nonExistingChannel_shouldThrow() {
    UUID id = UUID.randomUUID();
    when(channelRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> channelService.delete(id));
  }
}

