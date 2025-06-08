package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(List<T> content, int size, boolean hasNext, Long totalElements) {

}

