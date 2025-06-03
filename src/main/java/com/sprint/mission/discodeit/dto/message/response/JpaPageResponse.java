package com.sprint.mission.discodeit.dto.message.response;

import lombok.Builder;

import java.util.List;

/**
 * PackageName  : com.sprint.mission.discodeit.Dto.message
 * FileName     : MessageResponse
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Builder
public record JpaPageResponse(
        List<JpaMessageResponse> content,
        long number,
        long size,
        boolean hasNext,
        long totalElements
) {
}
