package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
@Tag(
    name = "Channel",
    description = "Channel API"
)
public class ChannelController {

  private final ChannelService channelService;

  @Operation(
      summary = "Public Channel 생성",
      operationId = "create_3"
  )
  @ApiResponse(
      responseCode = "201",
      description = "Public Channel이 성공적으로 생성됨",
      content = @Content(mediaType = "*/*", schema = @Schema(implementation = Channel.class))
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = PublicChannelCreateRequest.class)
      ),
      required = true
  )
  @PostMapping("/public")
  public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @Operation(
      summary = "Private Channel 생성",
      operationId = "create_4"
  )
  @ApiResponse(
      responseCode = "201",
      description = "Private Channel이 성공적으로 생성됨",
      content = @Content(mediaType = "*/*", schema = @Schema(implementation = Channel.class))
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = PrivateChannelCreateRequest.class)
      ),
      required = true
  )
  @PostMapping("/private")
  public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @Operation(
      summary = "Channel 정보 수정",
      operationId = "update_3",
      tags = {"Channel"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Channel 정보가 성공적으로 수정됨",
              content = @Content(mediaType = "*/*", schema = @Schema(implementation = Channel.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Private Channel은 수정할 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "Private channel cannot be updated")
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Channel을 찾을 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "Channel with id {channelId} not found")
              )
          )
      }
  )
  @Parameter(
      name = "channelId",
      in = ParameterIn.PATH,
      description = "수정할 Channel ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = PublicChannelUpdateRequest.class)
      )
  )
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> update(@PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel updatedChannel = channelService.update(channelId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @Operation(
      summary = "Channel 삭제",
      operationId = "delete_2",
      tags = {"Channel"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "Channel이 성공적으로 삭제됨"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Channel을 찾을 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "Channel with id {channelId} not found")
              )
          )
      }
  )
  @Parameter(
      name = "channelId",
      in = ParameterIn.PATH,
      description = "삭제할 Channel ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    channelService.deleteChannel(channelId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(
      summary = "User가 참여 중인 Channel 목록 조회",
      operationId = "findAll_1",
      tags = {"Channel"}
  )
  @ApiResponse(
      responseCode = "200",
      description = "Channel 목록 조회 성공",
      content = @Content(mediaType = "*/*",
          array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))
      )
  )
  @Parameter(
      name = "userId",
      in = ParameterIn.QUERY,
      description = "조회할 User ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(channels);
  }
}