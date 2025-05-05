package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 테스트 실행
        User user1 = userService.create("김민준");
        User user2 = userService.create("김이월");
        Channel channel = channelService.create("일반채널");
        Message msg1 = messageService.create(user1.getId(), channel.getId(), "안녕하세요!");
        Message msg2 = messageService.create(user2.getId(), channel.getId(), "반가워요!");
        System.out.println("유저 목록: " + userService.findAll());
        System.out.println("채널 목록: " + channelService.findAll());
        System.out.println("메시지 목록: " + messageService.findAll());
        messageService.update(msg1.getId(), "수정된 인사!");
        System.out.println("수정된 메시지: " + messageService.findById(msg1.getId()));
        messageService.delete(msg2.getId());
        System.out.println("삭제 후 메시지 목록: " + messageService.findAll());
    }
}

