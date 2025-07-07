package com.sprint.mission.discodeit.mapper;

import java.time.Instant;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.sprint.mission.discodeit.dto.response.PageResponse;

@Mapper
public interface PageResponseMapper {

  static <T> PageResponse<T> fromSlice(Page<T> page, Instant nextCursor) {
    return new PageResponse<>(
        page.getContent(),
        nextCursor != null ? nextCursor.toString() : null, // Instant를 문자열로 변환
        page.getSize(),
        page.hasNext(),
        page.getTotalElements());
  }
}
