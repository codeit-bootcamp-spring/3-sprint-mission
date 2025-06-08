package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-06T12:22:43+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
)
@Component
public class ChannelMapperImpl extends ChannelMapper {

    @Override
    public ChannelDto toDto(Channel channel) {
        if ( channel == null ) {
            return null;
        }

        ChannelDto channelDto = new ChannelDto();

        channelDto.setId( channel.getId() );
        channelDto.setType( channel.getType() );
        channelDto.setName( channel.getName() );
        channelDto.setDescription( channel.getDescription() );
        channelDto.setLastMessageAt( channel.getLastMessageAt() );

        return channelDto;
    }

    @Override
    public Channel channelDtoToChannel(ChannelDto channelDto) {
        if ( channelDto == null ) {
            return null;
        }

        Channel.ChannelBuilder channel = Channel.builder();

        channel.id( channelDto.getId() );
        channel.name( channelDto.getName() );
        channel.type( channelDto.getType() );
        channel.description( channelDto.getDescription() );
        channel.lastMessageAt( channelDto.getLastMessageAt() );

        return channel.build();
    }
}
