package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "채널", description = "채널 API")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final MessageRepository messageRepository;

  @Operation(
      summary = "Public 채널 생성"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "Public 채널 생성 성공", content = @Content(schema = @Schema(implementation = Channel.class)))
      }
  )
  @PostMapping("/public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody CreatePublicChannelRequest createPublicChannelRequest) {
    Channel publicChannel = channelService.create(createPublicChannelRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(publicChannel);
  }

  @Operation(
      summary = "Private 채널 생성"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "Private 채널 생성 성공", content = @Content(schema = @Schema(implementation = Channel.class)))
      }
  )
  @PostMapping("/private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody CreatePrivateChannelRequest createPrivateChannelRequest
  ) {
    Channel privateChannel = channelService.create(createPrivateChannelRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(privateChannel);
  }

  @Operation(
      summary = "Public 채널 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Public 채널 조회 성공", content = @Content(schema = @Schema(implementation = PublicChannelDTO.class))),
          @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없습니다", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @GetMapping("find/public/{channelId}")
  public ResponseEntity<PublicChannelDTO> findPublicChannel(
      @Parameter(description = "조회할 Public 채널 ID", required = true)
      @PathVariable("channelId") UUID channelId
  ) {
    Channel publicChannel = channelService.find(channelId);
    Instant lastMessageAt = getLastMessageAt(publicChannel.getId());
    return ResponseEntity.ok(PublicChannelDTO.fromDomain(publicChannel, lastMessageAt));
  }

  @Operation(
      summary = "Private 채널 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Private 채널 조회 성공", content = @Content(schema = @Schema(implementation = PrivateChannelDTO.class))),
          @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없습니다", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @GetMapping("find/private/{channelId}")
  public ResponseEntity<PrivateChannelDTO> findPrivateChannel(
      @Parameter(description = "조회할 Private 채널 ID", required = true)
      @PathVariable("channelId") UUID channelId) {
    Channel privateChannel = channelService.find(channelId);
    Instant lastMessageAt = getLastMessageAt(privateChannel.getId());
    return ResponseEntity.ok(PrivateChannelDTO.fromDomain(privateChannel, lastMessageAt));
  }


  @Operation(
      summary = "사용자가 참여 중인 채널 목록 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "채널 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDTO.class))))
      }
  )
  @GetMapping
  public ResponseEntity<List<ChannelDTO>> findAllChannelByUserId(
      @Parameter(description = "조회할 사용자 ID", required = true)
      @RequestParam("userId") UUID userId
  ) {
    List<Channel> channelList = channelService.findAllByUserId(userId);

    List<ChannelDTO> channelDTOs = channelList.stream()
        .map(channel -> {
          Instant lastMessageAt = getLastMessageAt(channel.getId());
          return ChannelDTO.fromDomain(channel, lastMessageAt);
        })
        .toList();

    return ResponseEntity.status(HttpStatus.OK).body(channelDTOs);
  }

  @Operation(
      summary = "채널 정보 수정"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "채널 수정 성공", content = @Content(schema = @Schema(implementation = Channel.class))),
          @ApiResponse(responseCode = "400", description = "Private 채널은 수정할 수 불가능", content = @Content(schema = @Schema(hidden = true))),
          @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> updateChannel(
      @Parameter(description = "수정할 채널 ID", required = true)
      @PathVariable("channelId") UUID channelId,
      @RequestBody UpdateChannelRequest updateChannelRequest
  ) {
    Channel channel = channelService.update(channelId, updateChannelRequest);
    Instant lastMessageAt = getLastMessageAt(channel.getId());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channel);
  }

  @Operation(
      summary = "채널 삭제"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "채널 삭제 성공"),
          @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(
      @Parameter(description = "삭제할 채널 ID", required = true)
      @PathVariable("channelId") UUID channelId
  ) {

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
