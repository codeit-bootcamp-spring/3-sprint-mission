package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

/**
 *  프로필 이미지 생성 DTO
 */

public record ProfileImageCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) { }
