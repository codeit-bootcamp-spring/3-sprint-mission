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

import java.util.Comparator;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        MessageService messageService = new JCFMessageService();
        ChannelService channelService = new JCFChannelService();

        System.out.println("유저 생성 및 조회");
        User user1 = new User("유저1");
        User user2 = new User("유저2");
        User user3 = new User("유저3");
        User user4 = new User("유저4");

        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        userService.createUser(user4);

        System.out.println("모든 유저 조회");
        userService.getAllUsers().stream()
                .sorted(Comparator.comparing(User::getUserName))
                .forEach(user -> System.out.println("Id : " + user.getId() + " 유저 이름 : " + user.getUserName() ));

        System.out.println();
        System.out.println("채널 생성 및 조회");
        Channel channel1 = new Channel("채널1");
        Channel channel2 = new Channel("채널2");

        channelService.createChannel(channel1);
        channelService.createChannel(channel2);

        channel1.addUser(user1.getId());
        channel1.addUser(user2.getId());
        channel2.addUser(user3.getId());
        channel2.addUser(user4.getId());

        System.out.println("채널 참가 유저 목록 조회");
        System.out.println("채널 : " + channel1.getChannelName());
        System.out.println(channel1.getUserIds());
        System.out.println("채널 : " + channel2.getChannelName());
        System.out.println(channel2.getUserIds());

        System.out.println();
        System.out.println("메시지 생성 및 조회");
        Message message1 = new Message("안녕하세요", user1.getId(), channel1.getId());
        Message message2 = new Message("여긴 채널1입니다", user2.getId(), channel1.getId());
        Message message3 = new Message("여긴 채널2입니다", user3.getId(), channel2.getId());
        Message message4 = new Message("테스트중", user4.getId(), channel2.getId());

        messageService.createMessage(message1);
        channelService.addMessageToChannel(channel1.getId(), message1.getId());
        messageService.createMessage(message2);
        channelService.addMessageToChannel(channel1.getId(), message2.getId());
        messageService.createMessage(message3);
        channelService.addMessageToChannel(channel2.getId(), message3.getId());
        messageService.createMessage(message4);
        channelService.addMessageToChannel(channel2.getId(), message4.getId());

        System.out.println("채널 : " + channel1.getChannelName() + "의 모든 메시지 목록");
        messageService.getAllMessages().stream()
                .filter(message -> message.getChannelId().equals(channel1.getId()))
                .forEach((message) -> {
                    System.out.println(message.getId() + " - " + message.getMsgContent());
                });

        System.out.println("채널 : " + channel2.getChannelName() + "의 모든 메시지 목록");
        messageService.getAllMessages().stream()
                .filter(message -> message.getChannelId().equals(channel2.getId()))
                .forEach((message) -> {
                    System.out.println(message.getId() + " - " + message.getMsgContent());
                });

        System.out.println();
        System.out.print("수정된 유저 정보 : ");
        System.out.print(userService.getUser(user1.getId()).getUserName());
        userService.updateUser(user1.getId(), "superuser1");
        System.out.print(" -> ");
        System.out.println(userService.getUser(user1.getId()).getUserName());
        System.out.println();

        System.out.print("수정된 채널 정보 : ");
        System.out.println(channelService.getChannel(channel2.getId()));
        channelService.updateChannel(channel2.getId(), "special channel");
        System.out.print(" -> ");
        System.out.println(channelService.getChannel(channel2.getId()));
        System.out.println();

        System.out.print("수정된 메시지 : ");
        System.out.print(messageService.getMessage(message1.getId()).getMsgContent());
        messageService.updateMessage(message1.getId(), "Update Message");
        System.out.print(" -> ");
        System.out.println(messageService.getMessage(message1.getId()).getMsgContent());
        System.out.println();

        userService.deleteUser(user4.getId());
        channelService.deleteChannel(channel1.getId());
        messageService.deleteMessage(message3.getId());

        System.out.println("삭제 후 남은 유저 목록");
        userService.getAllUsers().forEach(user -> System.out.println(user.getUserName()));
        System.out.println("삭제 후 남은 채널 목록");
        channelService.getAllChannels().forEach(channel -> System.out.println(channel.getChannelName()));
        System.out.println("삭제 후 남은 메시지 목록");
        messageService.getAllMessages().forEach(message -> System.out.println(message.getMsgContent()));

    }
}
