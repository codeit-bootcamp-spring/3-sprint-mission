package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse<T> {
    private final List<T> content;
    private final String nextCursor;
    private final int size;
    private final boolean hasNext;
    private final Long totalElements;

    public PageResponse(List<T> content, String nextCursor, int size, boolean hasNext, Long totalElements) {
        this.content = content;
        this.nextCursor = nextCursor;
        this.size = size;
        this.hasNext = hasNext;
        this.totalElements = totalElements;
    }
}