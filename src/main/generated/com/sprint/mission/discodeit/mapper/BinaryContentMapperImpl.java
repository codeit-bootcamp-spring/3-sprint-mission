package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T13:45:11+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
)
@Component
public class BinaryContentMapperImpl extends BinaryContentMapper {

    @Override
    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        BinaryContentDto binaryContentDto = new BinaryContentDto();

        binaryContentDto.setId( binaryContent.getId() );
        binaryContentDto.setFileName( binaryContent.getFileName() );
        binaryContentDto.setSize( binaryContent.getSize() );
        binaryContentDto.setContentType( binaryContent.getContentType() );

        return binaryContentDto;
    }

    @Override
    public BinaryContent binaryContentDtoToBinaryContent(BinaryContentDto binaryContentDto) {
        if ( binaryContentDto == null ) {
            return null;
        }

        BinaryContent.BinaryContentBuilder binaryContent = BinaryContent.builder();

        binaryContent.id( binaryContentDto.getId() );
        binaryContent.fileName( binaryContentDto.getFileName() );
        binaryContent.size( binaryContentDto.getSize() );
        binaryContent.contentType( binaryContentDto.getContentType() );

        return binaryContent.build();
    }
}
