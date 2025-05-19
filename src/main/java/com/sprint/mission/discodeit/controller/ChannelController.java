package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Channel", description = "Channel API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
  @PostMapping(path = "/public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody PublicChannelDTO publicChannelDTO) {
    Channel createdChannel = channelService.createPublicChannel(publicChannelDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @Operation(summary = "Private Channel 생성")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
  @PostMapping(path = "/private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody PrivateChannelDTO privateChannelDTO) {
    Channel createdChannel = channelService.createPrivateChannel(privateChannelDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }
  
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<ChannelResponseDTO>> findAllByUserId(
      @Parameter(description = "조회할 User ID") @RequestParam(required = true) UUID userId) {
    List<ChannelResponseDTO> channels = channelService.findAllByUserId(userId);

    return ResponseEntity.status(HttpStatus.OK).body(channels);
  }

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
          @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Channel with id {channelId} not found")})),
          @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Channel with id {channelId} not found")}))
      }
  )
  @PatchMapping(path = "/{channelId}")
  public ResponseEntity<ChannelResponseDTO> update(
      @Parameter(description = "수정할 Channel ID") @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateDTO publicChannelUpdateDTO) {
    ChannelResponseDTO updatedChannel = channelService.update(channelId, publicChannelUpdateDTO);

    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @Operation(summary = "Channel 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Channel with id {channelId} not found")}))
      }
  )
  @DeleteMapping(path = "/{channelId}")
  public ResponseEntity<String> deleteById(
      @Parameter(description = "삭제할 Channel ID") @PathVariable UUID channelId) {
    channelService.deleteById(channelId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
