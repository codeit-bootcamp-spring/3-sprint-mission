package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

//PageResponse<T>(List<T> content, int size, boolean hasNext, Long totalElements) {

@Component
public class PageResponseMapper {

  public <T> PageResponse<T> fromSlice(Slice<T> slice, Long totalCnt) {
    return new PageResponse<>(slice.getContent(), slice.getSize(),
        slice.hasNext(), totalCnt);
  }

  public <T> PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<>(page.getContent(), page.getSize(), page.hasNext(),
        page.getTotalElements());
  }

}
