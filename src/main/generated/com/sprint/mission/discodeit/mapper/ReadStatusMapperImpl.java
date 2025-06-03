package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T09:31:51+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Oracle Corporation)"
)
@Component
public class ReadStatusMapperImpl implements ReadStatusMapper {

    @Override
    public ReadStatusDto toDto(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }

        ReadStatusDto.ReadStatusDtoBuilder readStatusDto = ReadStatusDto.builder();

        readStatusDto.id( readStatus.getId() );
        readStatusDto.lastReadAt( readStatus.getLastReadAt() );

        readStatusDto.userId( readStatus.getUser().getId() );
        readStatusDto.channelId( readStatus.getChannel().getId() );

        return readStatusDto.build();
    }
}
