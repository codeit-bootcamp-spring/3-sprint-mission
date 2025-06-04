package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        Object nextCursor,
        int size, // page 크기
        boolean hasNext,
        Long totalElements
) {
}
