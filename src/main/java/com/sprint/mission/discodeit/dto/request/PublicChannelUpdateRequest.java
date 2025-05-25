package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "변경하려는 채널 정보")
// 파라미터 그룹화를 위해 작성하였으나, PRIVATE CHANNEL의 구현체에서는 사용 X
public record PublicChannelUpdateRequest(
    @Schema(description = "변경할 채널명")
    String newName,

    @Schema(description = "변경할 채널 설명", example = "이렇게 수정하고 싶어요")
    String newDescription
) {


}
