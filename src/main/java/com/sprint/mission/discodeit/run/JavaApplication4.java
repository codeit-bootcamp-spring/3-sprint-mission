package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class JavaApplication4 {
    public static void main(String[] args) {
//        // File*Repository 구현체 활용 테스트
//        System.out.println("-------------------File*Repository-------------------");
//        UserService basicUserService = new BasicUserService(new FileUserRepository());
//
//        // User 등록
//        User user1 = new User("Han", "han@gmail.com", "pwd1234", "Hi", true,
//                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//        User user2 = new User("Dong", "dong@gmail.com", "pwd12345", "Hello", true,
//                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//        User user3 = new User("Woo", "woo@gmail.com", "pwd12345", "Welcome", true,
//                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//
//        basicUserService.save(user1);
//        basicUserService.save(user2);
//        basicUserService.save(user3);
//        // User 조회
//        System.out.println("전체 User 목록: ");
//        basicUserService.findAll().forEach(System.out::println);
//
//        System.out.println("----------------------------");
//        System.out.println("User1 조회: ");
//        Optional<User> foundUser = basicUserService.findById(user1.getId());
//        System.out.println(foundUser);
//
//        System.out.println("----------------------------");
//        System.out.println("등록되지 않은 User 조회: ");
//        Optional<User> notExistUser = basicUserService.findById(UUID.randomUUID());
//        System.out.println(notExistUser);
//
//        // User 수정
//        user1.updateName("Han Dong Woo");
//        user2.updateEmail("dong@naver.com");
//        user3.updateIntroduction("Nice to meet you");
//        basicUserService.update(user1);
//        basicUserService.update(user2);
//        basicUserService.update(user3);
//        // 수정된 User 조회
//        System.out.println("----------------------------");
//        System.out.println("수정된 User 조회: ");
//        Optional<User> updatedUser1 = basicUserService.findById(user1.getId());
//        Optional<User> updatedUser2 = basicUserService.findById(user2.getId());
//        Optional<User> updatedUser3 = basicUserService.findById(user3.getId());
//        System.out.println(updatedUser1);
//        System.out.println(updatedUser2);
//        System.out.println(updatedUser3);
//        System.out.println("----------------------------");
//        System.out.println("친구 목록 기능 확인: ");
//        // 친구 추가
//        ((BasicUserService)basicUserService).addFriend(user1, user2);
//        ((BasicUserService)basicUserService).addFriend(user1, user3);
//
//        // 친구 삭제
//        ((BasicUserService)basicUserService).deleteFriend(user1, user3);
//        basicUserService.findAll().forEach(user -> {
//            System.out.println(user.getName() + "님의 친구 목록: ");
//            System.out.println(user.getFriends());
//            System.out.println("---------------------------------");
//        });
//
//        // User 삭제
//        basicUserService.deleteById(user3.getId());
//        // 조회를 통해 삭제 되었는지 확인
//        System.out.println("----------------------------");
//        System.out.println("User3 삭제 확인: ");
//        basicUserService.findAll().forEach(System.out::println);
//
//        System.out.println();
//
//        // 채널 등록
//        ChannelService basicChannelService = new BasicChannelService(new FileChannelRepository(), basicUserService);
//
//        Channel channel1 = new Channel("Ch.1", user1, "채널1입니다.", new ArrayList<>(),
//                new ArrayList<>());
//        Channel channel2= new Channel("channel2", user2, "It's Channel2",
//                new ArrayList<>(), new ArrayList<>());
//        Channel channel3 = new Channel("channel3", user2, "It's Channel3",
//                new ArrayList<>(), new ArrayList<>());
//
//        basicChannelService.save(channel1);
//        basicChannelService.save(channel2);
//        basicChannelService.save(channel3);
//
//        // Channel 조회
//        System.out.println("----------------------------");
//        System.out.println("전체 Channel 조회: ");
//        basicChannelService.findAll().forEach(System.out::println);
//
//        System.out.println("----------------------------");
//        System.out.println("Channel1 조회: ");
//        Optional<Channel> foundChannel = basicChannelService.findById(channel1.getId());
//        System.out.println(foundChannel);
//
//        System.out.println("----------------------------");
//        System.out.println("등록되지 않은 Channel 조회: ");
//        Optional<Channel> notExistChannel = basicChannelService.findById(UUID.randomUUID());
//        System.out.println(notExistChannel);
//
//        // Channel 수정
//        channel1.updateChannelName("Channel1");
//        channel2.updateDescription("채널2입니다.");
//        // 수정된 Channel 조회
//        System.out.println("----------------------------");
//        System.out.println("수정된 Channel 조회: ");
//        Optional<Channel> updatedChannel1 = basicChannelService.findById(channel1.getId());
//        Optional<Channel> updatedChannel2 = basicChannelService.findById(channel2.getId());
//
//        System.out.println(updatedChannel1);
//        System.out.println(updatedChannel2);
//
//        System.out.println("----------------------------");
//        System.out.println("User 추가, 삭제 기능 확인:");
//        ((BasicChannelService)basicChannelService).addUser(channel1, user2);
//        ((BasicChannelService)basicChannelService).addUser(channel2, user1);
//
//        ((BasicChannelService)basicChannelService).deleteUser(channel2, user1);
//        basicChannelService.findAll().forEach(channel -> {
//            System.out.println(channel.getChannelName() + "의 유저 목록:");
//            System.out.println(channel.getUserList());
//            System.out.println("------------------------------------");
//        });
//
//        // Channel 삭제
//        basicChannelService.deleteById(channel2.getId());
//        // 조회를 통해 삭제 되었는지 확인
//        basicChannelService.findAll().forEach(System.out::println);
//        // 삭제된 채널에 속해 있던 User의 channelList 확인
//        basicUserService.findById(user2.getId()).ifPresent(user -> {
//            System.out.println(user.getName() + "님의 채널 목록: ");
//            System.out.println(user.getChannels());
//            System.out.println("------------------------------------");
//        });
//
//        // 메시지 등록
//        MessageService basicMessageService = new BasicMessageService(new FileMessageRepository(), basicUserService,
//                basicChannelService);
//
//        Message message1 = new Message("안녕하세요.", user1, channel1);
//        Message message2 = new Message("Hi", user2, channel3);
//
//        basicMessageService.create(message1);
//        basicMessageService.create(message2);
//
//        // Message 조회
//        System.out.println("전체 메시지 목록: ");
//        basicMessageService.findAll().forEach(System.out::println);
//
//        System.out.println("----------------------------");
//        System.out.println("Message1 조회: ");
//        Optional<Message> foundMessage = basicMessageService.findById(message1.getId());
//        System.out.println(foundMessage);
//
//        System.out.println("----------------------------");
//        System.out.println("없는 메시지 조회: ");
//        Optional<Message> notExistMessage = basicMessageService.findById(UUID.randomUUID());
//        System.out.println(notExistMessage);
//
//        System.out.println("----------------------------");
//        System.out.println("User1이 보낸 메시지 목록: ");
//        basicMessageService.findMessagesByUserId(user1.getId()).forEach(System.out::println);
//
//        System.out.println("----------------------------");
//        System.out.println("Channel1에서 보낸 메시지 목록: ");
//        basicMessageService.findMessagesByChannelId(channel1.getId()).forEach(System.out::println);
//
//        System.out.println("----------------------------");
//        System.out.println("Hi가 포함된 메시지 목록: ");
//        basicMessageService.findMessageByContainingWord("Hi").forEach(System.out::println);
//
//        // Message 수정
//        message1.updateContent("Hello");
//
//        // 수정된 Message 조회
//        System.out.println("----------------------------");
//        System.out.println("수정된 Message 조회: ");
//        Optional<Message> updatedMessage = basicMessageService.findById(message1.getId());
//        System.out.println(updatedMessage);
//
//        // Message 삭제
//        basicMessageService.deleteById(message2.getId());
//        // 조회를 통해 삭제 되었는지 확인
//        System.out.println("----------------------------");
//        System.out.println("Message2 삭제 확인: ");
//        basicMessageService.findAll().forEach(System.out::println);
//
//        System.out.println("----------------------------");
//        System.out.println("삭제된 메시지와 연관된 User, Channel의 MessageList 확인: ");
//        basicUserService.findById(user2.getId()).ifPresent(user -> {
//            System.out.println(user.getName() + "님의 메시지 목록:");
//            System.out.println(user.getMessages());
//            System.out.println("----------------------------");
//        });
//
//        basicChannelService.findById(channel3.getId()).ifPresent(channel -> {
//            System.out.println(channel.getChannelName() + " 채널의 메시지 목록:");
//            System.out.println(channel.getMessageList());
//            System.out.println("----------------------------");
//        });
    }
}
