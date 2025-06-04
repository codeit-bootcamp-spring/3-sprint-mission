package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {

    private final BinaryContentStorage binaryContentStorage;

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if (binaryContent == null) {
            return null;
        }

        try (InputStream inputStream = binaryContentStorage.get(binaryContent.getId())) {
            byte[] bytes = inputStream.readAllBytes();

            return new BinaryContentDto(
                    binaryContent.getId(),
                    binaryContent.getFileName(),
                    binaryContent.getSize(),
                    binaryContent.getContentType(),
                    bytes
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
