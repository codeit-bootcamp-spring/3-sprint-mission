package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 사용자 정보 조회")
    ResponseEntity<UserDto> getCurrentUser(
        @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails);
} 