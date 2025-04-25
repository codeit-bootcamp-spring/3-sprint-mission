package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;


@SpringBootApplication
public class DiscodeitApplication {
//    static User setupUser(UserService userService) {
//        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "woody1234");
//        User user = userService.create(request, Optional.empty());
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
//        System.out.println("메시지 생성: " + message.getContent());
//        System.out.println("메시지 생성 시간 : " + message.getCreatedAt());
//    }
//
//    private static UserService userService;
//    private static ChannelService channelService;
//    private static MessageService messageService;
//
//
//    public static void main(String[] args) {
//        // 빈의 생성과 관계 설정을 Spring의 ApplicationContext로 하기 위한 context설정
//        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
//
//        //서비스 초기화
//        userService = context.getBean(UserService.class);
//        channelService = context.getBean(ChannelService.class);
//        messageService = context.getBean(MessageService.class);
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//    }



}
