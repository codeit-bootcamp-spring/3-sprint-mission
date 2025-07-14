package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(max = 100, message = "채팅방 이름은 100자 이하로 입력해주세요.")
    String newName,
    @Size(max = 500, message = "채팅방 소개는 500자 이하로 입력해주세요.")
    String newDescription
) {

}
