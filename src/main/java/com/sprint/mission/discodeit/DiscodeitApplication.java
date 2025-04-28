package com.sprint.mission.discodeit;

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

@SpringBootApplication
public class DiscodeitApplication {

    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());

    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
        // 서비스 초기화

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        //유저 테스트
        System.out.println("유저 추가 테스트");
        User user1 = userService.create("고희준", "kgmlwns904@gmail.com", "qwerasdf123");
        User user2 = userService.create("구희준", "kiiigmlwns904@gmail.com", "qwerzdfasdf123");
        System.out.println(user1.getUsername() + "유저를 추가했습니다 ");

        System.out.println("유저 전체 조회 테스트");
        System.out.println(userService.findAll());

        System.out.println("유저 수정 테스트");
        userService.update(user.getId(), "dongmin", "dongmin@gmail.com", "qwerasdf");
        System.out.println(userService.findAll());

        System.out.println(user1.getUsername() + "유저 삭제 테스트");
        userService.delete(user1.getId());
        System.out.println(userService.findAll());

        //채널 테스트
        System.out.println("채널 추가 테스트");
        Channel channel1 = channelService.create(ChannelType.PUBLIC, "스프링 스터디", "코드잇 스프린터들과 스프링 스터디방");
        System.out.println(channel1.getName() + "채널을 추가했습니다.");

        System.out.println("채널 전체 조회 테스트");
        System.out.println(channelService.findAll());

        System.out.println("채널 수정 테스트");
        channelService.update(channel1.getId(), "알고리즘 스터디", "코드잇 스프린터들과 알고리즘 스터디방");
        System.out.println(channelService.findAll());

        System.out.println("채널 삭제 테스트");
        System.out.println(channel.getName() + "의 채널을 삭제합니다");
        channelService.delete(channel.getId());
        System.out.println(channelService.findAll());

        //메시지 테스트
        System.out.println("메시지 추가 테스트");
        Message message1 = messageService.create("안녕하세요.", channel1.getId(), user.getId());
        Message message2 = messageService.create("하이헬로~", channel1.getId(), user2.getId());

        System.out.println("메시지 전체 조회 테스트");
        System.out.println(messageService.findAll());

        System.out.println("메시지 수정 테스트");
        messageService.update(message1.getId(), "Hello~");
        System.out.println(messageService.findAll());

        System.out.println("메시지 삭제 테스트");
        System.out.println(message2.getContent() + "의 메시지를 삭제합니다");
        messageService.delete(message2.getId());
        System.out.println(messageService.findAll());

        //messageCreateTest(messageService, channel, user);
    }

}
