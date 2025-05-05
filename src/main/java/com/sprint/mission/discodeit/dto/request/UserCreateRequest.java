package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContent;

public record UserCreateRequest(
    String email,
    String name,
    String password,
    BinaryContent profileImage
) {

}