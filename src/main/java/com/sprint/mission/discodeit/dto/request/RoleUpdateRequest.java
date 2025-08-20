package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RoleUpdateRequest(
	@NotNull(message = "userId는 필수 값입니다.")
	UUID userId,
	@NotNull(message = "역할(Role)은 필수 값입니다.")
	Role newRole
) {

}
