package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.respond.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageResponseMapper {

  public <T> PageResponse<T> fromSlice(Slice<T> slice) {
    PageResponse<T> response = new PageResponse<>();
    response.content = slice.getContent();
    response.hasNext = slice.hasNext();
    response.number = slice.getNumber();
    response.size = slice.getSize();
    response.totalElements = null;
    return response;
  }

  public <T> PageResponse<T> fromPage(Page<T> page) {
    PageResponse<T> response = new PageResponse<>();
    response.content = page.getContent();
    response.hasNext = page.hasNext();
    response.number = page.getNumber();
    response.size = page.getSize();
    response.totalElements = page.getTotalElements();
    return response;
  }

}
