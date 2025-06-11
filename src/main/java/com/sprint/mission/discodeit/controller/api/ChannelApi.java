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
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(
        responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
        content = @Content(schema = @Schema(implementation = ChannelDto.class))
    )
    ResponseEntity<ChannelDto> create(
        PublicChannelCreateRequest request
    );

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(
        responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
        content = @Content(schema = @Schema(implementation = ChannelDto.class))
    )
    ResponseEntity<ChannelDto> create(
        PrivateChannelCreateRequest request
    );

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(
        responseCode = "200", description = "Channel 목록 조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
    )
    ResponseEntity<List<ChannelDto>> findAll(
        @Parameter(description = "조회할 User ID") UUID userId
    );

    @Operation(summary = "Channel 정보 수정")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
            content = @Content(schema = @Schema(implementation = ChannelDto.class))
        ),
        @ApiResponse(
            responseCode = "404", description = "Channel을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
        ),
        @ApiResponse(
            responseCode = "400", description = "Private Channel은 수정할 수 없음",
            content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))
        )
    })
    ResponseEntity<ChannelDto> update(
        @Parameter(description = "수정할 Channel ID") UUID channelId,
        PublicChannelUpdateRequest request
    );

    @Operation(summary = "Channel 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
        @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    ResponseEntity<Void> delete(
        @Parameter(description = "삭제할 채널 ID") UUID channelId
    );
}
