package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T10:26:56+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BinaryContentMapper binaryContentMapper;

    @Override
    public MessageDto toDto(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageDto.MessageDtoBuilder messageDto = MessageDto.builder();

        messageDto.id( message.getId() );
        messageDto.createdAt( message.getCreatedAt() );
        messageDto.updatedAt( message.getUpdatedAt() );
        messageDto.content( message.getContent() );
        messageDto.author( userMapper.toDto( message.getAuthor() ) );
        messageDto.attachments( binaryContentListToBinaryContentDtoList( message.getAttachments() ) );

        messageDto.channelId( message.getChannel() != null ? message.getChannel().getId() : null );

        return messageDto.build();
    }

    protected List<BinaryContentDto> binaryContentListToBinaryContentDtoList(List<BinaryContent> list) {
        if ( list == null ) {
            return null;
        }

        List<BinaryContentDto> list1 = new ArrayList<BinaryContentDto>( list.size() );
        for ( BinaryContent binaryContent : list ) {
            list1.add( binaryContentMapper.toDto( binaryContent ) );
        }

        return list1;
    }
}
