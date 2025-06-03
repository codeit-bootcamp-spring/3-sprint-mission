package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

    public <T> PageResponse<T> fromPage(Page<T> page) {
        List<T> content = page.getContent();
        int size = page.getSize();
        boolean hasNext = page.hasNext();
        Long totalElements = page.getTotalElements();

        String nextCursor = null;

        return new PageResponse<>(content, nextCursor, size, hasNext, totalElements);
    }
}