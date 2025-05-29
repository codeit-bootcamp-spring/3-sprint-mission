package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

    public BinaryContentResponseDto toDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(binaryContent.getId(), binaryContent.getFileName(),
                binaryContent.getSize(), binaryContent.getContentType());
    }
}
