package com.sprint.mission.discodeit.refactor;

import com.sprint.mission.discodeit.refactor.entity.Channel2;
import com.sprint.mission.discodeit.refactor.entity.Message2;
import com.sprint.mission.discodeit.refactor.entity.User2;
import com.sprint.mission.discodeit.refactor.service.ChannelService2;
import com.sprint.mission.discodeit.refactor.service.MessageService2;
import com.sprint.mission.discodeit.refactor.service.UserService2;
import com.sprint.mission.discodeit.refactor.service.jcf.JcfChannelService2;
import com.sprint.mission.discodeit.refactor.service.jcf.JcfMessageService2;
import com.sprint.mission.discodeit.refactor.service.jcf.JcfUserService2;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor
 * fileName       : JavaAppJcf
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JavaAppJcf {
    private static final UserService2 userService = new JcfUserService2();
    public static final ChannelService2 channelService = new JcfChannelService2();
    public static final MessageService2 messageService = new JcfMessageService2(userService, channelService);


    public static void main(String[] args) {

        // c
        System.out.println("\n-create");
        User2 user = userService.createUser("user1");
        User2 user2 = userService.createUser("user2");
        User2 user3 = userService.createUser("user3");
        // r
        System.out.println("\n-read: all");
        userService.findAllUsers().forEach(u -> System.out.println(u.getUsername()));
        System.out.println("read: single");
        System.out.println(userService.findUserById(user.getId()).getUsername());
        // u
        System.out.println("\n-update");
        userService.updateUser(user2.getId(), "new user");
        // d
        System.out.println("\n-delete");
        userService.deleteUser(user.getId());
        //
        System.out.println("\n-all");
        userService.findAllUsers().forEach(u -> System.out.println(u.getUsername()));


        System.out.println("\n-create");
        Channel2 channel = channelService.createChannel("test channel 1");
        Channel2 channel2 = channelService.createChannel("test channel 2");
        Channel2 channel3 = channelService.createChannel("test channel 3");

        System.out.println("\n-read: all");
        channelService.findAllChannel().forEach(c -> System.out.println(c.getName()));
        System.out.println("-read: single");
        System.out.println(channelService.findChannelById(channel.getId()).getName());

        System.out.println("\n-update");
        channelService.updateChannel(channel2.getId(), "updated channel");

        System.out.println("\n-delete");
        channelService.deleteChannel(channel.getId());
        channelService.findChannelById(channel.getId());
        System.out.println("\n-all");
        channelService.findAllChannel().forEach(c -> System.out.println(c.getName()));


        System.out.println("\n-create");
        Message2 message1 = messageService.createMessage(user2.getId(), channel2.getId(), "my first message from korea");
        Message2 message2 = messageService.createMessage(user3.getId(), channel3.getId(), "my first message from America");

        System.out.println("\n-read: all");
        messageService.findAllMessages().forEach(m -> System.out.println(m.getContent()));
        System.out.println("-read: single");
        System.out.println(messageService.findMessageById(message1.getId()).getContent());

        System.out.println("\n-update");
        messageService.updateMessage(message2.getId(),"my second message from Japan");

        System.out.println("\n-delete");
        messageService.deleteMessage(message1.getId());

        System.out.println("\n-all");
        messageService.findAllMessages().forEach(m -> System.out.println(m.getContent()));

    }
}
