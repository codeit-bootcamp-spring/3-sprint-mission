package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class PageResponseMapper {

    public <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
            slice.getContent(),
            slice.getNumber(),
            slice.getSize(),
            slice.hasNext(),
            null
        );
    }

    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getTotalElements()
        );
    }
}
