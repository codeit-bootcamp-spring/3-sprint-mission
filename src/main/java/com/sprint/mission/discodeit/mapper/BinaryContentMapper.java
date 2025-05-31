package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.serviceDto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        UUID id = binaryContent.getId();
        String fileName = binaryContent.getFileName();
        String contentType = binaryContent.getContentType();
        Long size = binaryContent.getSize();
        byte[] bytes = binaryContent.getBytes();

        return new BinaryContentDto(
            id,
            fileName,
            size,
            contentType,
            bytes);
    }

}
