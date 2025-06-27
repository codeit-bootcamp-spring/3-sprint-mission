package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateDto(
        @NotBlank(message = "채널 이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "채널 이름은 최대 100자까지 입력 가능합니다.")
        String newName,

        @Size(max = 500, message = "채널에 대한 설명은 최대 500자까지 입력 가능합니다.")
        String newDescription) {

}
