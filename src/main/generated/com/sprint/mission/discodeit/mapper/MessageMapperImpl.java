package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-06T12:22:43+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
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

        UUID channelId = null;
        UserDto author = null;
        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        String content = null;
        List<BinaryContentDto> attachments = null;

        channelId = messageChannelId( message );
        author = userMapper.toDto( message.getUser() );
        id = message.getId();
        createdAt = message.getCreatedAt();
        updatedAt = message.getUpdatedAt();
        content = message.getContent();
        attachments = binaryContentListToBinaryContentDtoList( message.getAttachments() );

        MessageDto messageDto = new MessageDto( id, createdAt, updatedAt, content, channelId, author, attachments );

        return messageDto;
    }

    @Override
    public Message messageDtoToMessage(MessageDto messageDto) {
        if ( messageDto == null ) {
            return null;
        }

        Message.MessageBuilder message = Message.builder();

        message.id( messageDto.id() );
        message.createdAt( messageDto.createdAt() );
        message.updatedAt( messageDto.updatedAt() );
        message.content( messageDto.content() );
        message.attachments( binaryContentDtoListToBinaryContentList( messageDto.attachments() ) );

        return message.build();
    }

    private UUID messageChannelId(Message message) {
        Channel channel = message.getChannel();
        if ( channel == null ) {
            return null;
        }
        return channel.getId();
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

    protected List<BinaryContent> binaryContentDtoListToBinaryContentList(List<BinaryContentDto> list) {
        if ( list == null ) {
            return null;
        }

        List<BinaryContent> list1 = new ArrayList<BinaryContent>( list.size() );
        for ( BinaryContentDto binaryContentDto : list ) {
            list1.add( binaryContentMapper.binaryContentDtoToBinaryContent( binaryContentDto ) );
        }

        return list1;
    }
}
