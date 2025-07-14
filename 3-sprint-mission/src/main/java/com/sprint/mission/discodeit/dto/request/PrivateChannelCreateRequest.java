package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull
    @NotEmpty(message = "초대할 사용자를 1명 이상 선택하세요.")
    List<UUID> participantIds
) {

}
