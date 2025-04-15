package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.*;

public class JavaApplication {

    public static void main(String[] args) {
        // 기본 요구사항
        // User 등록
        UserService userService = new JCFUserService();
        User user1 = new User("name1", "email1@email.com", "pwd123", "Hi",
                true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        User user2 = new User("name2", "email2@email.com", "pwd1234", "Hello",
                true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        User user3 = new User("name3", "email3@email.com", "pwd12345", "Nice",
                true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        User user4 = new User("name4", "email4@email.com", "pwd123456", "I'm Kim",
                true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        userService.save(user1);
        userService.save(user2);
        userService.save(user3);
        userService.save(user4);
        // User 조회
        System.out.println("전체 User 목록: ");
        userService.findAll().forEach(System.out::println);

        System.out.println("----------------------------");
        System.out.println("User1 조회: ");
        Optional<User> foundUser = userService.findById(user1.getId());
        System.out.println(foundUser);

        System.out.println("----------------------------");
        System.out.println("등록되지 않은 User 조회: ");
        Optional<User> notExistUser = userService.findById(UUID.randomUUID());
        System.out.println(notExistUser);
        // User 수정
        user1.updateName("name11");
        user2.updateEmail("email22@email.com");
        user3.updateIntroduction("Nice to meet you");
        userService.update(user1);
        userService.update(user2);
        userService.update(user3);
        // 수정된 User 조회
        System.out.println("----------------------------");
        System.out.println("수정된 User 조회: ");
        Optional<User> updatedUser1 = userService.findById(user1.getId());
        Optional<User> updatedUser2 = userService.findById(user2.getId());
        Optional<User> updatedUser3 = userService.findById(user3.getId());
        System.out.println(updatedUser1);
        System.out.println(updatedUser2);
        System.out.println(updatedUser3);
        // User 삭제
        userService.deleteById(user4.getId());
        // 조회를 통해 삭제 되었는지 확인
        System.out.println("----------------------------");
        System.out.println("User4 삭제 확인: ");
        userService.findAll().forEach(System.out::println);

        System.out.println();
        // Channel 등록
        ChannelService channelService = new JCFChannelService(userService);
        Channel channel1 = new Channel("channel1", user1, "It's Channel1",
                new ArrayList<>(), new ArrayList<>());
        Channel channel2= new Channel("channel2", user2, "It's Channel2",
                new ArrayList<>(), new ArrayList<>());
        Channel channel3 = new Channel("channel3", user1, "It's Channel3",
                new ArrayList<>(), new ArrayList<>());
        Channel channel4 = new Channel("channel4", user2,"It's Channel4",
                new ArrayList<>(), new ArrayList<>());
        Channel channel5 = new Channel("channel5", user3, "It's Channel5",
                new ArrayList<>(), new ArrayList<>());

        channelService.save(channel1);
        channelService.save(channel2);
        channelService.save(channel3);
        channelService.save(channel4);
        channelService.save(channel5);
        // Channel 조회
        System.out.println("----------------------------");
        System.out.println("전체 Channel 조회: ");
        channelService.findAll().forEach(System.out::println);

        System.out.println("----------------------------");
        System.out.println("Channel1 조회: ");
        Optional<Channel> foundChannel = channelService.findById(channel1.getId());
        System.out.println(foundChannel);

        System.out.println("----------------------------");
        System.out.println("등록되지 않은 Channel 조회: ");
        Optional<Channel> notExistChannel = channelService.findById(UUID.randomUUID());
        System.out.println(notExistChannel);
        // Channel 수정
        channel1.updateChannelName("Ch1");
        channel2.updateDescription("Welcome to channel2");

        channelService.update(channel1);
        channelService.update(channel2);
        // 수정된 Channel 조회
        System.out.println("----------------------------");
        System.out.println("수정된 Channel 조회: ");
        Optional<Channel> updatedChannel1 = channelService.findById(channel1.getId());
        Optional<Channel> updatedChannel2 = channelService.findById(channel2.getId());

        System.out.println(updatedChannel1);
        System.out.println(updatedChannel2);

        // Channel 삭제
        channelService.deleteById(channel5.getId());
        // 조회를 통해 삭제 되었는지 확인
        System.out.println("----------------------------");
        System.out.println("Channel5 삭제 확인: ");
        channelService.findAll().forEach(System.out::println);

        System.out.println();
        // Message 등록
        MessageService messageService = new JCFMessageService(userService, channelService);
        Message message1 = new Message("Hello, World!", user1, channel1);
        Message message2 = new Message("Hello", user2, channel2);
        Message message3 = new Message("Hi", user3, channel3);
        Message message4 = new Message("Thank you", user1, channel4);
        Message message5 = new Message("Nice to meet you", user2, channel1);

        messageService.create(message1);
        messageService.create(message2);
        messageService.create(message3);
        messageService.create(message4);
        messageService.create(message5);
        // Message 조회
        System.out.println("----------------------------");
        System.out.println("전체 메시지 목록: ");
        messageService.findAll().forEach(System.out::println);

        System.out.println("----------------------------");
        System.out.println("Message1 조회: ");
        Optional<Message> foundMessage = messageService.findById(message1.getId());
        System.out.println(foundMessage);

        System.out.println("----------------------------");
        System.out.println("없는 메시지 조회: ");
        Optional<Message> notExistMessage = messageService.findById(UUID.randomUUID());
        System.out.println(notExistMessage);

        System.out.println("----------------------------");
        System.out.println("User1이 보낸 메시지 목록: ");
        messageService.findMessagesByUserId(user1.getId()).forEach(System.out::println);

        System.out.println("----------------------------");
        System.out.println("Channel1에서 보낸 메시지 목록: ");
        messageService.findMessagesByChannelId(channel1.getId()).forEach(System.out::println);

        System.out.println("----------------------------");
        System.out.println("Hello가 포함된 메시지 목록: ");
        messageService.findMessageByContainingWord("Hello").forEach(System.out::println);
        // Message 수정
        message3.updateContent("Hi, I'm Han");
        messageService.update(message3);
        // 수정된 Message 조회
        System.out.println("----------------------------");
        System.out.println("수정된 Message 조회: ");
        Optional<Message> updatedMessage = messageService.findById(message3.getId());

        System.out.println(updatedMessage);
        // Message 삭제
        messageService.deleteById(message5.getId());
        // 조회를 통해 삭제 되었는지 확인
        System.out.println("----------------------------");
        System.out.println("Message5 삭제 확인: ");
        messageService.findAll().forEach(System.out::println);

        // 심화 요구사항
        System.out.println("----------------------------");
        Channel channel6 = new Channel("channel6", user4, "It's channel6",
                new ArrayList<>(), new ArrayList<>());

        // 삭제된 user를 채널 주인으로 설정하여 채널 생성 시도
        try {
            channelService.save(channel6);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        Message message6 = new Message("안녕하세요", user4, channel1);
        System.out.println("----------------------------");
        // 삭제된 user를 sender로 설정하여 메시지 생성 시도
        try {
            messageService.create(message6);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        Message message7 = new Message("반갑습니다.", user1, channel5);
        System.out.println("----------------------------");
        // 삭제된 user를 sender로 설정하여 메시지 생성 시도
        try {
            messageService.create(message7);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
