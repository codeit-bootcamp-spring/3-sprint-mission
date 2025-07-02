package com.sprint.mission.discodeit.dto.Channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

        @NotBlank(message = "공개 채널 이름은 빈 값일 수 없습니다.")
        @Size(max = 100, message = "공개 채널 이름은 최대 100자입니다.")
        String newName,

        @Size(max = 500, message = "공개 채널 설명은 최대 500자입니다.")
        String newDescription
) {

}
