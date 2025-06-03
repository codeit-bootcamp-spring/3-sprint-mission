package com.sprint.mission.discodeit.dto.message.response;

import lombok.Builder;

import java.util.List;

/**
 * PackageName  : com.sprint.mission.discodeit.dto.message
 * FileName     : AdvancedJpaPageResponse
 * Author       : dounguk
 * Date         : 2025. 6. 2.
 */
@Builder
public record AdvancedJpaPageResponse (
        List<JpaMessageResponse> content,
        Object nextCursor,
        int size,
        boolean hasNext,
        Long totalElements
){
}
