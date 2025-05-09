package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JCFMessageServiceTest {

    UserService userService;
    ChannelService channelService;
    MessageService messageService;
    User user;
    User creator;
    Channel channel;
    Message message;

    @BeforeEach
    void setUp() {
        userService = new JCFUserService();
        channelService = new JCFChannelService();
        messageService = new JCFMessageService(userService, channelService);
        user = userService.create("이주용", "sjo06102", "!qwe0123", "sjo06102@gmail.com");
        creator = userService.create("심슨", "simpson1", "!qwe0123", "sjo06102@naver.com");
        channel = channelService.create("스프링필드", "심슨네 가족", false, creator, "1234");
        message = messageService.create(user, channel, "Why you little!!!!!!!!!!");
    }

    @Test
    void createMessage() {
        User createUser = userService.create("바트", "simpson3", "!qwe0123", "sjo06102@hufs.ac.kr");
        Message createMessage = messageService.create(createUser, channel, "ascklasck~~!~!~");

        assertAll(
                () -> assertNotNull(createMessage, "메세지가 생성되지 않았습니다."),
                () -> assertEquals(createUser, createMessage.getUser(), "보낸이가 다릅니다"),
                () -> assertEquals(channel, createMessage.getChannel(), "채널이 다릅니다")
        );
    }

    @Test
    void findMessageByChannelId() {

        List<Message> messageList = messageService.readByChannelId(channel.getId());

        assertNotNull(messageList, "채널에 메세지가 존재하지 않습니다.");
        ;

    }

    @Test
    void findAllMessages() {

        List<Message> messageList = messageService.readAll();

        assertNotNull(messageList);

    }

    @Test
    void updateMessage() {

        messageService.update(message.getId(), "Why you little!!!!!!!!!!".toUpperCase());

        assertAll(
                () -> assertNotNull(message, "메세지가 Null입니다."),
                () -> assertEquals("Why you little!!!!!!!!!!".toUpperCase(), messageService.readById(message.getId()).getContent())
        );
    }

    @Test
    void deleteMessage() {

        messageService.deleteById(message.getId());
        assertNull(messageService.readById(message.getId()));
    }

}
