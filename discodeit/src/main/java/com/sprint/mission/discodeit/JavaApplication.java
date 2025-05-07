//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
//import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
//import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//public class JavaApplication {
//    static User setupUser(UserService userService) {
//        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "woody1234");
//        User user = userService.create(request, Optional.empty());
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        CreatePublicChannelRequest request = new CreatePublicChannelRequest("공지","공지 채널입니다.", Optional.empty());
//        Channel channel = channelService.create(request);
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        CreateMessageRequest request = new CreateMessageRequest("안녕하세요, 반갑습니다.", channel.getId(), author.getId());
//        Message message = messageService.create(request, new ArrayList<>());
//        System.out.println("메시지 생성: " + message.getContent());
//        System.out.println("메시지 생성 시간 : " + message.getCreatedAt());
//    }
//
//    public static void main(String[] args) {
//        // 레포지토리 초기화
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();
//
//        // 서비스 초기화
//        UserService userService = new BasicUserService(userRepository);
//        ChannelService channelService = new BasicChannelService(channelRepository);
//        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//    }
//}
