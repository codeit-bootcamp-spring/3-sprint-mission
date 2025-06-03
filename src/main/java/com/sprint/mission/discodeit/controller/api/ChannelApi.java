package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성\n")
  @ApiResponse(
      responseCode = "201",
      description = "Public Channel이 성공적으로 생성됨",
      content = @Content(
          mediaType = "*/*",
          schema = @Schema(implementation = ChannelDto.class)
      )
  )
  ResponseEntity<ChannelDto> createPublic(
      @Parameter(description = "Public Channel 생성 정보") PublicChannelCreateRequest request
  );

  @Operation(summary = "Private Channel 생성")
  @ApiResponse(
      responseCode = "201",
      description = "Private Channel이 성공적으로 생성됨",
      content = @Content(
          mediaType = "*/*",
          schema = @Schema(implementation = ChannelDto.class)
      )
  )
  ResponseEntity<ChannelDto> createPrivate(
      @Parameter(description = "Private Channel 생성 정보") PrivateChannelCreateRequest request
  );

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(
              mediaType = "*/*",
              schema = @Schema(implementation = ChannelDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Channel with id {channelId} not found")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Private Channel은 수정할 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Private channel cannot be updated")
          )
      )
  })
  ResponseEntity<ChannelDto> update(
      @Parameter(
          name = "channelId",
          description = "수정할 Channel ID",
          required = true) UUID channelId,
      @Parameter(description = "수정할 Channel 정보") PublicChannelUpdateRequest request
  );

  @Operation(summary = "Channel 삭제")
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Channel이 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Channel with id {channelId} not found")
          )
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(
          name = "channelId",
          description = "삭제할 Channel ID",
          required = true) UUID channelId
  );

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Channel 목록 조회 성공",
          content = @Content(
              mediaType = "*/*",
              array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))
          )
      )
  })
  ResponseEntity<List<ChannelDto>> findAllByUserId(
      @Parameter(name = "userId", description = "조회할 User ID", required = true) UUID userId
  );
}
