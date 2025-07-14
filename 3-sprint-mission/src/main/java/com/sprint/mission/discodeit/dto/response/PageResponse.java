package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(
    List<T> content, //실제 데이터
    Object nextCursor, // 페이지 넘버
    int size, // 페이지 크기(한번에 조회되는 메시지 수?)
    boolean hasNext,
    Long totalElements // T 데이터의 총 개수. null 가능
) {

}
