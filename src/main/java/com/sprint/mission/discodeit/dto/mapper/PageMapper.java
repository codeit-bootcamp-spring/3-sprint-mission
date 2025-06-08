package com.sprint.mission.discodeit.dto.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PageMapper {

  /**
   * Slice 객체를 PageResponse로 변환 (totalElements는 null)
   */
  public <T, R> PageResponse<R> toPageResponse(Slice<T> slice, Function<T, R> mapper) {
    List<R> content = slice.getContent().stream()
        .map(mapper)
        .toList();

    return new PageResponse<>(
        content,
        slice.getNumber(),
        slice.getSize(),
        slice.hasNext(),
        null // Slice는 총 개수를 제공하지 않음
    );
  }

  /**
   * Page 객체를 PageResponse로 변환 (totalElements 포함)
   */
  public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
    List<R> content = page.getContent().stream()
        .map(mapper)
        .toList();

    return new PageResponse<>(
        content,
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements());
  }

  /**
   * Slice 객체를 PageResponse로 변환 (이미 변환된 content 사용)
   */
  public <T> PageResponse<T> toPageResponse(Slice<?> slice, List<T> content) {
    return new PageResponse<>(
        content,
        slice.getNumber(),
        slice.getSize(),
        slice.hasNext(),
        null);
  }

  /**
   * Page 객체를 PageResponse로 변환 (이미 변환된 content 사용)
   */
  public <T> PageResponse<T> toPageResponse(Page<?> page, List<T> content) {
    return new PageResponse<>(
        content,
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements());
  }
}