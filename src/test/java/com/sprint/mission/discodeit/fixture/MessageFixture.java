package com.sprint.mission.discodeit.fixture;

import static com.sprint.mission.discodeit.fixture.UserFixture.createUserDto;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class MessageFixture {

    private static Instant fixedNow = Instant.parse("2025-01-01T01:00:00Z");

    public static Message createMessage(String content, Channel ch, User user) {
        Message message = new Message(content, ch, user, null);
        ReflectionTestUtils.setField(message, "id", UUID.randomUUID());
        return message;
    }

    public static Message createMessage(String content, Channel channel, User author,
            Instant createdAt) {
        Message msg = new Message(content, channel, author, new ArrayList<>());
        ReflectionTestUtils.setField(msg, "createdAt", createdAt);
        return msg;
    }

    public static MessageDto createMessageDto(Message message, Channel ch, User user,
            Instant time) {
        UserDto userDto = createUserDto(user);
        return new MessageDto(
                message.getId(), time, time, message.getContent(),
                ch.getId(), userDto, new ArrayList<>()
        );
    }

    public static MessageDto createMessageDtoWithAttachments(Message message, Channel ch, User user,
            List<BinaryContentDto> attachments) {
        UserDto userDto = createUserDto(user);
        return new MessageDto(message.getId(), fixedNow, fixedNow,
                message.getContent(), ch.getId(), userDto, attachments);
    }
}
