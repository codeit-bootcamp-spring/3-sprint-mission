package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record MessageCreateRequest(

    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Pattern(regexp = "^(?!\\s*$).+", message = "메시지는 공백만으로 구성될 수 없습니다")
    String content,

    @NotNull(message = "채널 ID는 필수입니다")
    UUID channelId,

    @NotNull(message = "작성자 ID는 필수입니다")
    UUID authorId
) {

}
