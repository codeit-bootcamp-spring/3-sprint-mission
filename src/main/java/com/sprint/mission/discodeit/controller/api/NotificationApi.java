package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface NotificationApi {
    @Operation(summary = "알림 조회")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "알림이 성공적으로 조회됨",
            content = @Content(schema = @Schema(implementation = NotificationDto.class))
        ),
        @ApiResponse(
            responseCode = "401", description = "인증되지 않은 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<List<NotificationDto>> findAll();

    @Operation(summary = "알림 확인")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", description = "알림이 성공적으로 확인 처리됨"
        ),
        @ApiResponse(
            responseCode = "401", description = "인증되지 않은 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403", description = "인가되지 않은 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", description = "알림이 존재하지 않음",
            content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> confirm(@NotNull @PathVariable UUID notificationId);
}