package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
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
        CreateUserRequest request2 = new CreateUserRequest("김현기","kinggusrl3@codeit.com","kinggusrl3");
        UpdateUserRequest update = new UpdateUserRequest("KHG","kinggusrl3@naver.com","kkk");
        User user = userService.create(request, Optional.empty());
        User user2 = userService.create(request2, Optional.empty());
        System.out.println(" 1번 유저명 : " + user.getUsername());
        userService.update(user2.getId(),update,Optional.empty() );
        System.out.println(" 변경된 2번 유저 이메일 조회 : " + user2.getEmail());
        userService.delete(user2.getId());
        System.out.println("전체 유저 조회 : " + userService.findAll() );
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
        UpdateMessageRequest updateMessageRequest = new UpdateMessageRequest("곧 점심이네요, 배가 고픕니다.");
        Message message = messageService.create(request, new ArrayList<>());
        System.out.println("메시지 생성 : " + message.getContent());
        System.out.println("메시지 생성 시간 : " + message.getCreatedAt());
        messageService.update(message.getId(),updateMessageRequest);
        System.out.println("수정된 메시지 생성 : " + message.getContent());
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

//

}
