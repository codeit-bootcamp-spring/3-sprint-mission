package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 서비스 초기화 (File 기반)
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService(userService);
        MessageService messageService = new FileMessageService(channelService, userService);

        // 테스트
        User user = handleUser(userService);
        Channel channel = handleChannel(channelService, user);
        handleMessage(messageService, channel, user);
    }


    private static User handleUser(UserService userService) {
        System.out.println("\n=== 유저 ===");

        // Create
        User user = userService.createUser("user");

        // Read
        System.out.println("[유저 단건 조회]");
        System.out.println(userService.getUser(user.getId()));
        System.out.println("[유저 전체 조회]");
        userService.getAllUsers().forEach(System.out::println);

        // Update
        System.out.println("[수정된 유저]");
        System.out.println(userService.updateUser(user, "updatedUser"));

//        // Delete
//        userService.deleteUser(user);
//        System.out.println("[삭제 후 유저 조회]");
//        userService.getAllUsers().forEach(System.out::println);

        return user;
    }

    public static Channel handleChannel(ChannelService channelService, User user) {
        System.out.println("\n=== 채널 ===");

        // Create
        Channel channel = channelService.createChannel("created channel", user.getId());

        // Read
        System.out.println("[채널 단건 조회]");
        System.out.println(channelService.getChannel(channel.getId()));
        System.out.println("[채널 전체 조회]");
        channelService.getAllChannels().forEach(System.out::println);

        // Update
        System.out.println("[수정된 채널]");
        System.out.println(channelService.updateChannel(channel, "updated channel"));

//        // Delete
//        channelService.deleteChannel(channel);
//        System.out.println("[삭제 후 채널 조회]");
//        channelService.getAllChannels().forEach(System.out::println);

        return channel;
    }

    public static void handleMessage(MessageService messageService, Channel channel, User user) {
        System.out.println("\n=== 메시지 ===");

        // Create
        Message message = messageService.createMessage("created message", channel.getId(), user.getId());

        // Read
        System.out.println("[메시지 단건 조회]");
        System.out.println(messageService.getMessage(message.getId()));
        System.out.println("[메시지 전체 조회]");
        messageService.getAllMessages().forEach(System.out::println);

        // Update
        System.out.println("[수정된 메시지]");
        System.out.println(messageService.updateMessage(message,"updated message"));

        // Delete
        messageService.deleteMessage(message);
        System.out.println("[삭제 후 메시지 조회]");
        messageService.getAllMessages().forEach(System.out::println);
    }
}
