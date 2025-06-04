package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    @PostMapping("/public")
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest req) {
        Channel channel = channelService.create(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channel);
    }

    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    @PostMapping("/private")
    public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest req) {
        Channel channel = channelService.create(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channel);
    }

    @Operation(summary = "단일 채널 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<ChannelDto> find(@PathVariable UUID id) {
        ChannelDto dto = channelService.get(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
        List<ChannelDto> list = channelService.getAllByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Channel 정보 수정", operationId = "update_3")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
    })
    @PatchMapping("/public/{channelId}")
    public ResponseEntity<Channel> update(@PathVariable UUID channelId, @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel udpatedChannel = channelService.update(channelId, publicChannelUpdateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(udpatedChannel);
    }

    @Operation(summary = "Channel 삭제", operationId = "delete_2")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
    })
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
//
//    @Operation(summary = "채널 접속")
//    @ApiResponse(responseCode = "200",  description = "접속 성공")
//    @PostMapping("/{channelId}/join")
//    public ResponseEntity<Void> join(
//            @PathVariable UUID channelId,
//            @RequestParam UUID userId
//    ) {
//        channelService.join(userId, channelId);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(summary = "채널 탈퇴")
//    @ApiResponse(responseCode = "200",  description = "탈퇴 성공")
//    @PostMapping("/{channelId}/leave")
//    public ResponseEntity<Void> leave(
//            @PathVariable UUID channelId,
//            @RequestParam UUID userId
//    ) {
//        channelService.leave(userId, channelId);
//        return ResponseEntity.ok().build();
//    }
}
