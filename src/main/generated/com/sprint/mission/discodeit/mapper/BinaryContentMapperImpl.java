package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-04T00:05:26+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (OpenLogic)"
)
@Component
public class BinaryContentMapperImpl implements BinaryContentMapper {

    @Override
    public BinaryContentResponse toResponse(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        UUID id = null;
        String fileName = null;
        String contentType = null;
        Long size = null;

        id = binaryContent.getId();
        fileName = binaryContent.getFileName();
        contentType = binaryContent.getContentType();
        size = binaryContent.getSize();

        BinaryContentResponse binaryContentResponse = new BinaryContentResponse( id, fileName, contentType, size );

        return binaryContentResponse;
    }
}
