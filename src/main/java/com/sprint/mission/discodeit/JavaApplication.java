package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        /* 등록 */
        // 유저 생성
        User user1 = userService.createUser(new User("user1"));
        User user2 = userService.createUser(new User("user2"));
        User user3 = userService.createUser(new User("user3"));

        // 채널 생성
        Channel channel1 = channelService.createChannel(new Channel("channel1", user1));
        Channel channel2 = channelService.createChannel(new Channel("channel2", user2));
        Channel channel3 = channelService.createChannel(new Channel("channel3", user3));

        // 메시지 생성
        Message message1 = messageService.createMessage(new Message(user1, channel1, "message by user1"));
        Message message2 = messageService.createMessage(new Message(user2, channel2, "message by user2"));
        Message message3 = messageService.createMessage(new Message(user3, channel3, "message by user3"));


        /* 조회 */
        System.out.println("\n========= 조회 =========");
        // 유저 단건 조회
        System.out.println("[유저 단건 조회]");
        System.out.println(userService.getUser(user1.getId()));
        // 유저 전체 조회
        System.out.println("[유저 전체 조회]");
        userService.getAllUsers().forEach(System.out::println);

        // 채널 단건 조회
        System.out.println("[채널 단건 조회]");
        System.out.println(channelService.getChannel(channel1.getId()));
        // 채널 전체 조회
        System.out.println("[채널 전체 조회]");
        channelService.getAllChannels().forEach(System.out::println);

        // 메시지 단건 조회
        System.out.println("[메시지 단건 조회]");
        System.out.println(messageService.getMessage(message1.getId()));
        // 메시지 전체 조회
        System.out.println("[메시지 전체 조회]");
        messageService.getAllMessages().forEach(System.out::println);
        // 채널 별 메시지 조회
        System.out.println("[채널 별 메시지 조회]");
        messageService.getMessagesByChannel(channel1.getId()).forEach(System.out::println);

        /* 수정 */
        System.out.println("\n===== 수정된 데이터 조회 =====");
        // 유저 수정
        System.out.println("[수정된 유저]");
        System.out.println(userService.updateUser(user1, "userA"));

        // 채널 수정
        System.out.println("[수정된 채널]");
        System.out.println(channelService.updateChannel(channel2, "user2's channel"));

        // 메시지 수정
        System.out.println("[수정된 메시지]");
        System.out.println(messageService.updateMessage(message3,"updated message3"));

        /* 삭제 */
        System.out.println("\n========= 삭제 =========");
        // 유저 삭제
        userService.deleteUser(user3);
        // 유저 전체 조회
        System.out.println("[유저 전체 조회]");
        userService.getAllUsers().forEach(System.out::println);

        // 메시지 삭제
        messageService.deleteMessage(message3);
        // 메시지 조회
        System.out.println("[메시지 전체 조회]");
        messageService.getAllMessages().forEach(System.out::println);

        // 채널 삭제
        channelService.deleteChannel(channel1);
        // 채널 조회
        System.out.println("[채널 전체 조회]");
        channelService.getAllChannels().forEach(System.out::println);
    }
}
