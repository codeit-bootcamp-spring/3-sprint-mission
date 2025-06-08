package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-07T12:54:42+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
)
@Component
public class ReadStatusMapperImpl implements ReadStatusMapper {

    @Override
    public ReadStatusDto toDto(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }

        UUID userId = null;
        UUID channelId = null;
        UUID id = null;
        Instant lastReadAt = null;

        userId = readStatusUserId( readStatus );
        channelId = readStatusChannelId( readStatus );
        id = readStatus.getId();
        lastReadAt = readStatus.getLastReadAt();

        ReadStatusDto readStatusDto = new ReadStatusDto( id, userId, channelId, lastReadAt );

        return readStatusDto;
    }

    @Override
    public ReadStatus readStatusDtoToReadStatus(ReadStatusDto readStatusDto) {
        if ( readStatusDto == null ) {
            return null;
        }

        ReadStatus.ReadStatusBuilder readStatus = ReadStatus.builder();

        readStatus.id( readStatusDto.id() );
        readStatus.lastReadAt( readStatusDto.lastReadAt() );

        return readStatus.build();
    }

    private UUID readStatusUserId(ReadStatus readStatus) {
        User user = readStatus.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private UUID readStatusChannelId(ReadStatus readStatus) {
        Channel channel = readStatus.getChannel();
        if ( channel == null ) {
            return null;
        }
        return channel.getId();
    }
}
