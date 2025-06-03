package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T09:31:51+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Oracle Corporation)"
)
@Component
public class BinaryContentMapperImpl implements BinaryContentMapper {

    @Override
    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        BinaryContentDto.BinaryContentDtoBuilder binaryContentDto = BinaryContentDto.builder();

        binaryContentDto.id( binaryContent.getId() );
        binaryContentDto.fileName( binaryContent.getFileName() );
        binaryContentDto.size( binaryContent.getSize() );
        binaryContentDto.contentType( binaryContent.getContentType() );

        return binaryContentDto.build();
    }
}
