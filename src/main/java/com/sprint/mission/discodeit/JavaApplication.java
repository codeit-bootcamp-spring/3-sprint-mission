package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(channelService, userService);

        // 기존 테스트
//        handleUser(userService);
//        handleChannel(channelService);
//        handleMessage(messageService);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService, user);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }

    static User setupUser(UserService userService) {
        User user = userService.createUser("okodee");
        return user;
    }

    static Channel setupChannel(ChannelService channelService, User user) {
        Channel channel = channelService.createChannel("공지방", user.getId());
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User user) {
        Message message = messageService.createMessage("안녕하세요.", channel.getId(), user.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    private static void handleUser(UserService userService) {
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

        // Delete
        userService.deleteUser(user);
        System.out.println("[삭제 후 유저 조회]");
        userService.getAllUsers().forEach(System.out::println);
    }

    public static void handleChannel(ChannelService channelService) {
        System.out.println("\n=== 채널 ===");

        // Create
        UUID userId = UUID.randomUUID();
        Channel channel = channelService.createChannel("createdChannel", userId);

        // Read
        System.out.println("[채널 단건 조회]");
        System.out.println(channelService.getChannel(channel.getId()));
        System.out.println("[채널 전체 조회]");
        channelService.getAllChannels().forEach(System.out::println);

        // Update
        System.out.println("[수정된 채널]");
        System.out.println(channelService.updateChannel(channel, "updatedChannel"));

        // Delete
        channelService.deleteChannel(channel);
        System.out.println("[삭제 후 채널 조회]");
        channelService.getAllChannels().forEach(System.out::println);
    }

    public static void handleMessage(MessageService messageService) {
        System.out.println("\n=== 메시지 ===");

        // Create
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Message message = messageService.createMessage("created message", channelId, userId);

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
