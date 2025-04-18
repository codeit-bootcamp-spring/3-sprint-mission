package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JcfChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

/**
 * packageName    : com.sprint.mission.discodeit.refactor
 * fileName       : JavaAppFile
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JavaAppFile {

    private static final JcfUserRepository userRepository = new JcfUserRepository();
    private static final JcfChannelRepository channelRepository = new JcfChannelRepository();
    private static final JcfMessageRepository messageRepository = new JcfMessageRepository();

//    private static final FileUserRepository userRepository = new FileUserRepository();
//    private static final FileChannelRepository channelRepository = new FileChannelRepository();
//    private static final FileMessageRepository messageRepository = new FileMessageRepository();



    private static final UserService userService = new BasicUserService(userRepository);
    public static final ChannelService channelService = new BasicChannelService(channelRepository);
    public static final MessageService messageService = new BasicMessageService(userService, channelService, messageRepository);

    public static void main(String[] args) {

        // c
        System.out.println("\n-create");
        User user = userService.createUser("user1");
        User user2 = userService.createUser("user2");
        User user3 = userService.createUser("user3");
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
        Channel channel = channelService.createChannel("test channel 1");
        Channel channel2 = channelService.createChannel("test channel 2");
        Channel channel3 = channelService.createChannel("test channel 3");

        System.out.println("\n-read: all");
        channelService.findAllChannel().forEach(c -> System.out.println(c.getName()));
        System.out.println("-read: single");
        System.out.println(channelService.findChannelById(channel.getId()).getName());

        System.out.println("\n-update");
        channelService.updateChannel(channel2.getId(), "updated channel");

        System.out.println("\n-delete");
        channelService.deleteChannel(channel.getId());

        System.out.println("\n-all");
        channelService.findAllChannel().forEach(c -> System.out.println(c.getName()));


        System.out.println("\n-create");
        Message message1 = messageService.createMessage(user2.getId(), channel2.getId(), "my first message from korea");
        Message message2 = messageService.createMessage(user3.getId(), channel3.getId(), "my first message from America");

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
