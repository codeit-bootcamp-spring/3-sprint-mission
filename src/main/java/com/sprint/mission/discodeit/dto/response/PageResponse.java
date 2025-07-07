package com.sprint.mission.discodeit.dto.response;

import java.util.List;

/**
 * 페이지네이션 응답 DTO
 * 
 * @param <T> 컨텐츠 타입
 */
public record PageResponse<T>(
                List<T> content,
                String nextCursor, // 커서는 문자열로 통일 (ISO timestamp, encoded value 등)
                int size,
                boolean hasNext,
                Long totalElements) {

}
