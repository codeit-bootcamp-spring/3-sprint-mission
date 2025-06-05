package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper
public interface PageResponseMapper {

  static <T> PageResponse<T> fromSlice(Page<T> page, Instant nextCursor) {
    return new PageResponse<>(
        page.getContent(),
        nextCursor,
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }
}

