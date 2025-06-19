package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
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

    private final BinaryContentStorage binaryContentStorage;

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if (binaryContent == null) {
            return null;
        }
        UUID id = binaryContent.getId();
        String fileName = binaryContent.getFileName();
        String contentType = binaryContent.getContentType();
        Long size = binaryContent.getSize();

        return new BinaryContentDto(
            id,
            fileName,
            size,
            contentType);
    }
}
