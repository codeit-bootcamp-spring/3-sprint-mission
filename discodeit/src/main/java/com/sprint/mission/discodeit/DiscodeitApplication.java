package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Optional;


@SpringBootApplication
public class DiscodeitApplication {
    static User setupUser(UserService userService) {
        CreateUserRequest request = new CreateUserRequest("woody", "woody@codeit.com", "woody1234");
        User user = userService.create(request, Optional.empty());
        System.out.println(" 유저명 : " + user.getUsername());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest("공지","공지 채널입니다.", Optional.empty());
        Channel channel = channelService.create(request);
        System.out.println(" 채널명 : " + channel.getChannelName());
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        CreateMessageRequest request = new CreateMessageRequest("안녕하세요, 반갑습니다.", channel.getId(), author.getId());
        Message message1 = messageService.create(request, new ArrayList<>());
        System.out.println("메시지 생성 : " + message1.getContent());
        System.out.println("메시지 생성 시간 : " + message1.getCreatedAt());
//        messageService.update(message1.getId(),"아아");
    }



    public static void main(String[] args) {
        // 빈의 생성과 관계 설정을 Spring의 ApplicationContext로 하기 위한 context설정
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        //서비스 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);


        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);


    }



}
