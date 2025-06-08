package com.sprint.mission.discodeit.dto.respond;

import java.util.List;

public class PageResponse<T> {

  public List<T> content;
  public Object nextCursor;
  public int size;
  public boolean hasNext;
  public Long totalElements;
}
