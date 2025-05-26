package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entitiy.Channel;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

  private final ChannelService channelService;

  //publicChannel 생성
  @Operation(summary = "Public Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Channel.class))),
  })
  @PostMapping(path = "/channels/public")
  public ResponseEntity<Channel> create_3(
      @RequestBody PublicChannelCreateRequest request) {
    Channel channel = channelService.create(
        new PublicChannelCreateRequest(request.name(), request.description()));
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  //privateChannel 생성
  @Operation(summary = "Private Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Channel.class))),
  })
  @PostMapping(path = "/channels/private")
  public ResponseEntity<Channel> create_4(
      @RequestBody PrivateChannelCreateRequest request) {
    Channel channel = channelService.create(
        new PrivateChannelCreateRequest(request.participantIds()));
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  //Channel 수정
  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel with id {channelId} not found"))),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음", content = @Content(schema = @Schema(example = "Private channel cannot be updated"))),
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Channel.class)))
  })
  @Parameter(name = "channelId", description = "수정할 ChannelID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @PatchMapping(path = "/channels/{channelId}")
  public ResponseEntity<String> updatePublicChannel(@PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    channelService.update(channelId, request);
    return ResponseEntity.status(HttpStatus.OK).body("수정 완료!");
  }

  //Channel 삭제
  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel with id {channelId} not found"))),
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨")
  })
  @Parameter(name = "channelId", description = "삭제할 Channel ID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @DeleteMapping("/channels/{channelId}")
  public ResponseEntity<String> delete_2(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
  }

  //특정 사용자의 Channel 조회
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))))
  })
  @Parameter(name = "userId", description = "조회할 User ID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @GetMapping("/channels")
  @ResponseBody
  public ResponseEntity<List<ChannelDto>> findAll_1(@RequestParam UUID userId) {
    List<ChannelDto> allByUserId = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
  }


}
