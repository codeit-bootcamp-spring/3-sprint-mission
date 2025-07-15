package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

    public <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor) {
        return new PageResponse<T>(
            slice.getContent(),
            nextCursor,
            slice.getSize(),
            slice.hasNext(),
            null
        );
    }

    public <T> PageResponse<T> fromPage(Page<T> page, Object nextCursor) {
        return new PageResponse<T>(
            page.getContent(),
            nextCursor,
            page.getSize(),
            page.hasNext(),
            page.getTotalElements()
        );
    }
}