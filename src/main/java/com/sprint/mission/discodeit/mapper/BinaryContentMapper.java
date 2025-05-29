package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent entity) {
        if (entity == null) {
            return null;
        }

        return BinaryContentDto.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .size(entity.getSize())
                .contentType(entity.getContentType())
                .bytes(entity.getBytes())
                .build();
    }
}