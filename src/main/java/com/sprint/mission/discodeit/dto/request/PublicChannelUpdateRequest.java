package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

    @Size(max = 50, message = "채널 이름은 50자 이내여야 합니다.")
    String newName,

    @Size(max = 255, message = "채널 설명은 255자 이내여야 합니다.")
    String newDescription

) {

}
