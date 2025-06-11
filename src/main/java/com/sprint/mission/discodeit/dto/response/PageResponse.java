package com.sprint.mission.discodeit.dto.response;

import java.util.List;

/*
* - `content`: 실제 데이터입니다.
- `number`: 페이지 번호입니다.
- `size`: 페이지의 크기입니다.
- `totalElements`: T 데이터의 총 갯수를 의미하며, null일 수 있습니다.
* */

public record PageResponse<T>(
    List<T> content,
    Object nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
) {

}
