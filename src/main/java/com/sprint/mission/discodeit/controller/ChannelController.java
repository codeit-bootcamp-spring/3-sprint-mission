package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping(path = "/public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody PublicChannelDTO publicChannelDTO) {
    Channel createdChannel = channelService.createPublicChannel(publicChannelDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @PostMapping(path = "/private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody PrivateChannelDTO privateChannelDTO) {
    Channel createdChannel = channelService.createPrivateChannel(privateChannelDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @GetMapping(path = "/{channelId}")
  public ResponseEntity<ChannelResponseDTO> findById(@PathVariable UUID channelId) {
    ChannelResponseDTO foundChannel = channelService.findById(channelId);

    return ResponseEntity.status(HttpStatus.OK).body(foundChannel);
  }

  @GetMapping(path = "/name")
  public ResponseEntity<List<ChannelResponseDTO>> findByNameContaining(@RequestParam String name) {
    List<ChannelResponseDTO> foundChannels = channelService.findByNameContaining(name);

    return ResponseEntity.status(HttpStatus.OK).body(foundChannels);
  }

  @GetMapping
  public ResponseEntity<List<ChannelResponseDTO>> findAll(
      @RequestParam(required = false) UUID userId) {
    List<ChannelResponseDTO> channels;

    if (userId != null) {
      channels = channelService.findAllByUserId(userId);
    } else {
      channels = channelService.findAll();
    }

    return ResponseEntity.status(HttpStatus.OK).body(channels);
  }

  @PatchMapping(path = "/{channelId}")
  public ResponseEntity<ChannelResponseDTO> update(@PathVariable UUID channelId,
      @RequestBody PublicChannelDTO publicChannelDTO) {
    ChannelResponseDTO updatedChannel = channelService.update(channelId, publicChannelDTO);

    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @DeleteMapping(path = "/{channelId}")
  public ResponseEntity<String> deleteById(@PathVariable UUID channelId) {
    channelService.deleteById(channelId);

    return ResponseEntity.status(HttpStatus.OK).body("[Success]: 채널 삭제 성공!");
  }

  @PostMapping(path = "/users")
  public ResponseEntity<String> inviteUser(
      @RequestBody ChannelMemberRequestDTO channelMemberRequestDTO) {
    channelService.inviteUser(channelMemberRequestDTO);

    return ResponseEntity.status(HttpStatus.OK).body("[Success]: 유저 초대 성공!");
  }

  @DeleteMapping(path = "/users")
  public ResponseEntity<String> kickUser(
      @RequestBody ChannelMemberRequestDTO channelMemberRequestDTO) {
    channelService.kickUser(channelMemberRequestDTO);

    return ResponseEntity.status(HttpStatus.OK).body("[Success]: 유저 추방 성공!");
  }
}
