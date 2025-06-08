package com.sprint.mission.discodeit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableBuilder {

  public static Pageable builder(int page, int size, String sortField, String sortDirection) {

    Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;

    return PageRequest.of(page, size, Sort.by(direction, sortField));
  }
}
