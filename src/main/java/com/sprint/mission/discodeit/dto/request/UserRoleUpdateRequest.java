package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleUpdateRequest(

    @NotNull(message = "수정하려는 유저의 id는 필수입니다")
    UUID userId,

    @NotNull(message = "수정될 역할이 입력되어야 합니다.")
    Role newRole
) {

}
