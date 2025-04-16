//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
//import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
//import com.sprint.mission.discodeit.service.jcf.JCFUserService;
//
//import java.util.*;
//
//public class JavaApplication {
//    static void userCRUDTest(UserService userService) { // UserService 객체를 파라미터로 받음
//        // User 생성
//        User user1 = userService.createUser("111111-2222222", "Alice", 20, "alice@gmail.com");
//        System.out.println("user1 생성: " + user1.getId());
//        User user2 = userService.createUser("111111-2223333", "John", 25, "john@gmail.com");
//        System.out.println("user2 생성: " + user2.getId());
//        User user3 = userService.createUser("111111-2224444", "Steve", 30, "steve@gmail.com");
//        System.out.println("user3 생성: " + user3.getId());
//
//        // User 조회
//        User readingUser1 = userService.readUser(user1.getId());
//        System.out.println("user1 조회: " + readingUser1.getId());
//        User readingUser2 = userService.readUser(user2.getId());
//        System.out.println("user2 조회: " + readingUser2.getId());
//        User readingUser3 = userService.readUser(user3.getId());
//        System.out.println("user3 조회: " + readingUser3.getId());
//
//        // 모든 User 조회
//        List<User> readAllUsers = userService.readAllUsers();
//        System.out.println("유저 조회: " + readAllUsers.size());
//
//        // User 수정
//        User updatingUser1 = userService.updateUser(user1.getId(), null,  "alice@naver.com");
//        System.out.println("user1 email 수정: " + String.join(",", updatingUser1.getEmail()));
//        User updatingUser2 = userService.updateUser(user2.getId(), "Johnny",  null);
//        System.out.println("user2 name, email 수정: " + String.join(",", updatingUser2.getName()));
//        User updatingUser3 = userService.updateUser(user3.getId(), "Wonder", "steve@yahoo.co.kr");
//        System.out.println("user3 name, email 수정: " + String.join(",", updatingUser2.getName(), updatingUser2.getEmail()));
//
//        // User 삭제
//        userService.deleteUser(user1.getId());
//        List<User> readUsersAfterDelete = userService.readAllUsers();
//        System.out.println("user1 삭제: " + readUsersAfterDelete.size());
////        userService.deleteUser(user2.getId());
////        System.out.println("user2 삭제: " + readUsersAfterDelete.size());
////        userService.deleteUser(user3.getId());
////        System.out.println("user3 삭제: " + readUsersAfterDelete.size());
//
//        System.out.println("User CRUD Test Completed");
//    }
//
//
//    static void channelCRUDTest(ChannelService channelService) { // ChannelService 객체를 파라미터로 받음
//        // Channel 생성
//        Channel channel1 = channelService.createChannel("여자친구", "사랑방");
//        System.out.println("channel1 생성: " + channel1.getId());
//        Channel channel2 = channelService.createChannel("엄마", "안부방" );
//        System.out.println("channel2 생성: " + channel2.getId());
//        Channel channel3 = channelService.createChannel("아빠", "위로방" );
//        System.out.println("channel3 생성: " + channel3.getId());
//
//        // Channel 조회
//        Channel readingChannel1 = channelService.readChannel(channel1.getId());
//        System.out.println("channel1 조회: " + readingChannel1.getId());
//        Channel readingChannel2 = channelService.readChannel(channel2.getId());
//        System.out.println("channel2 조회: " + readingChannel2.getId());
//        Channel readingChannel3 = channelService.readChannel(channel3.getId());
//        System.out.println("channel3 조회: " + readingChannel3.getId());
//
//        // 모든 Channel 조회
//        List<Channel> readAllChannels = channelService.readAllChannels();
//        System.out.println("채널 조회: " + readAllChannels.size());
//
//        // Channel 수정
//        Channel updatingChannel1 = channelService.updateChannel(channel1.getId(), null,  "찐사랑방");
//        System.out.println("channel1 description 수정: " + String.join(",", updatingChannel1.getDescription()));
//        Channel updatingChannel2 = channelService.updateChannel(channel2.getId(), "아버지",  null);
//        System.out.println("channel2 name 수정: " + String.join(",", updatingChannel2.getChannelName()));
//        Channel updatingChannel3 = channelService.updateChannel(channel3.getId(), "어머니",  "위로방");
//        System.out.println("channel3 name, description 수정: " + String.join(",", updatingChannel3.getChannelName(), updatingChannel3.getDescription()));
//
//        // User 삭제
//        channelService.deleteChannel(channel1.getId());
//        List<Channel> readChannelsAfterDelete = channelService.readAllChannels();
//        System.out.println("channel1 삭제: " + readChannelsAfterDelete.size());
////        channelService.deleteChannel(channel2.getId());
////        System.out.println("channel2 삭제: " + readChannelsAfterDelete.size());
////        channelService.deleteChannel(channel3.getId());
////        System.out.println("channel3 삭제: " + readChannelsAfterDelete.size());
//
//        System.out.println("Channel CRUD Test Completed");
//    }
//
//    static void messageCRUDTest(MessageService messageService) { // MessageService 객체를 파라미터로 받음
//        // 단독 테스트를 하려면 channelId와 authorId가 필요함
//        UUID channelId = UUID.randomUUID();
//        UUID authorId = UUID.randomUUID();
//        // Message 생성
//        Message message1 = messageService.createMessage("나는 너를 정말 사랑해", channelId, authorId);
//        System.out.println("message1 생성: " + message1.getId());
//        Message message2 = messageService.createMessage("나는 너를 정말 애정해", channelId, authorId);
//        System.out.println("message2 생성: " + message2.getId());
//        Message message3 = messageService.createMessage("나는 너를 정말 연모해", channelId, authorId);
//        System.out.println("message3 생성: " + message3.getId());
//
//        // Message 조회
//        Message readingMessage = messageService.readMessage(message1.getId());
//        System.out.println("message1 조회: " + readingMessage.getId());
//        Message readingMessage2 = messageService.readMessage(message2.getId());
//        System.out.println("message2 조회: " + readingMessage2.getId());
//        Message readingMessage3 = messageService.readMessage(message3.getId());
//        System.out.println("message3 조회: " + readingMessage3.getId());
//
//        // 모든 Message 조회
//        List<Message> readAllMessages = messageService.readAllMessages();
//        System.out.println("채널 조회: " + readAllMessages.size());
//
//        // Message 수정
//        Message updatingMessage1 = messageService.updateMessage(message1.getId(), "나는 너의 모든 것이 좋아");
//        System.out.println("message content 수정: " + String.join(",", updatingMessage1.getContent()));
//        Message updatingMessage2 = messageService.updateMessage(message2.getId(), "나는 너의 모든 것을 사랑해");
//        System.out.println("message content 수정: " + String.join(",", updatingMessage2.getContent()));
//        Message updatingMessage3 = messageService.updateMessage(message3.getId(), "나는 너의 모든 면을 사랑해");
//        System.out.println("message content 수정: " + String.join(",", updatingMessage3.getContent()));
//
//        // Message 삭제
//        messageService.deleteMessage(message1.getId());
//        List<Message> readMessagesAfterDelete = messageService.readAllMessages();
//        System.out.println("message1 삭제: " + readMessagesAfterDelete.size());
////        messageService.deleteMessage(message2.getId());
////        System.out.println("message2 삭제: " + readMessagesAfterDelete.size());
////        messageService.deleteMessage(message3.getId());
////        System.out.println("message3 삭제: " + readMessagesAfterDelete.size());
//
//        System.out.println("Message CRUD Test Completed");
//    }
//
//    static User setupUser(UserService userService) {
//        User user = userService.createUser("123456-5678910", "임정현", 25, "1234wjdgusdl@gmail.com");
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.createChannel("스프링 백엔드 3기", "이곳은 코드잇 스프린트 스프링 백엔드 3기 채널입니다.");
//        return channel;
//    }
//
//    static Message setupMessage(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.createMessage("다들 좋은 아침입니다.", channel.getId(), author.getId());
//        return message;
//    }
//    public static void main(String[] args) {
//        // 서비스 초기화
//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService(channelService, userService);
//
//        // 테스트
////        userCRUDTest(userService);
////        channelCRUDTest(channelService);
////        messageCRUDTest(messageService);
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        setupMessage(messageService, channel, user);
//    }
//}
