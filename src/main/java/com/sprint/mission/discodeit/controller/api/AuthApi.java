package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

	@Operation(summary = "사용자 역할 변경", description = "ADMIN만 호출 가능")
	@io.swagger.v3.oas.annotations.responses.ApiResponses({
		@ApiResponse(responseCode = "200", description = "변경 성공",
			content = @Content(schema = @Schema(implementation = UserDto.class))),
		@ApiResponse(responseCode = "400", description = "검증 실패"),
		@ApiResponse(responseCode = "401", description = "로그인 정보 없음"),
		@ApiResponse(responseCode = "403", description = "관리자 권한 없음")
	})
	@SecurityRequirement(name = "JSESSIONID")
	@PutMapping("/role")
	ResponseEntity<UserDto> updateRole(@Valid @RequestBody RoleUpdateRequest request);
}