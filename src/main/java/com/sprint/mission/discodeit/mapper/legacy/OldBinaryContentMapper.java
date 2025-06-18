package com.sprint.mission.discodeit.mapper.legacy;

import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : BinaryContentMapper
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Component
public class OldBinaryContentMapper {

    public JpaBinaryContentResponse toDto(BinaryContent binaryContent) {
        if(binaryContent == null) return null;

        return JpaBinaryContentResponse.builder()
                .id(binaryContent.getId())
                .fileName(binaryContent.getFileName())
                .size(binaryContent.getSize())
                .contentType(binaryContent.getContentType())
                .build();
    }
}
