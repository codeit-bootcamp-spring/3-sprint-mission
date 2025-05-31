package com.sprint.mission.discodeit.dto.message;

import java.util.List;

/**
 * PackageName  : com.sprint.mission.discodeit.Dto.message
 * FileName     : MessageResponse
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
public record MessageResponse(
        List<String> content,
        long number,
        long size,
        boolean hasNext,
        long totalElements
) {

}
