package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  public <T> PageResponse<T> fromSlice(Slice<T> slice) {
    return PageResponse.<T>builder()
        .content(slice.getContent())
        .nextCursor(calculateNextCursor(slice))
        .size(slice.getSize())
        .hasNext(slice.hasNext())
        .totalElements(null)
        .build();
  }

  public <T> PageResponse<T> fromPage(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .nextCursor(calculateNextCursor(page))
        .size(page.getSize())
        .hasNext(page.hasNext())
        .totalElements(page.getTotalElements())
        .build();
  }

  private <T> Object calculateNextCursor(Slice<T> slice) {
    if (!slice.hasNext() || slice.getContent().isEmpty()) {
      return null;
    }

    T lastElement = slice.getContent().get(slice.getContent().size() - 1);

    if (lastElement instanceof MessageDto) {
      return ((MessageDto) lastElement).createdAt();
    }
    return null;
  }

}
