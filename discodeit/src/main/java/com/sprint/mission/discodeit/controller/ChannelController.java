package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final MessageRepository messageRepository;

  @PostMapping("/public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody CreatePublicChannelRequest createPublicChannelRequest) {
    Channel publicChannel = channelService.create(createPublicChannelRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(publicChannel);
  }

  @PostMapping("/private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody CreatePrivateChannelRequest createPrivateChannelRequest) {
    Channel privateChannel = channelService.create(createPrivateChannelRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(privateChannel);
  }

  //  @RequestMapping(value = "/public", method = RequestMethod.GET)
  @GetMapping("find/public/{channelId}")
  public ResponseEntity<PublicChannelDTO> findPublicChannel(
      @PathVariable("channelId") UUID channelId) {
    Channel publicChannel = channelService.find(channelId);
    Instant lastMessageAt = getLastMessageAt(publicChannel.getId());
    return ResponseEntity.ok(PublicChannelDTO.fromDomain(publicChannel, lastMessageAt));
  }

  @GetMapping("find/private/{channelId}")
  public ResponseEntity<PrivateChannelDTO> findPrivateChannel(
      @PathVariable("channelId") UUID channelId) {
    Channel privateChannel = channelService.find(channelId);
    Instant lastMessageAt = getLastMessageAt(privateChannel.getId());
    return ResponseEntity.ok(PrivateChannelDTO.fromDomain(privateChannel, lastMessageAt));
  }

  @GetMapping
  public ResponseEntity<List<ChannelDTO>> findAllChannelByUserId(
      @RequestParam("userId") UUID userId) {
    List<Channel> channelList = channelService.findAllByUserId(userId);

    List<ChannelDTO> channelDTOs = channelList.stream()
        .map(channel -> {
          Instant lastMessageAt = getLastMessageAt(channel.getId());
          return ChannelDTO.fromDomain(channel, lastMessageAt);
        })
        .toList();

    return ResponseEntity.status(HttpStatus.OK).body(channelDTOs);
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> updateChannel(@PathVariable("channelId") UUID channelId,
      @RequestBody UpdateChannelRequest updateChannelRequest) {
    Channel channel = channelService.update(channelId, updateChannelRequest);
    Instant lastMessageAt = getLastMessageAt(channel.getId());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channel);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<String> deleteChannel(@PathVariable("channelId") UUID channelId) {

    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }


  private Instant getLastMessageAt(UUID channelId) { // 가장 최근 메시지 생성 시간을 가져오는 메서드
    return messageRepository.findAllByChannelId(channelId)
        .stream()
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .map(Message::getCreatedAt)
        .limit(1)
        .findFirst()
        .orElse(Instant.MIN);
  }

}
