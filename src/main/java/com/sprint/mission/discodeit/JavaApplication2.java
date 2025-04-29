package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;

import java.util.*;

public class JavaApplication2 {
    public static void main(String[] args) {
        // 서비스 초기화
        // TODO Basic*Service 구현체를 초기화하세요.
        UserRepository userRepository;
        UserService userService;
        BasicUserService basicUserService;
//        ChannelService channelService;
//        MessageService messageService;

        // 셋업
        User user = setupUser(basicUserService);
//        Channel channel = setupChannel(channelService);
        // 테스트
//        messageCreateTest(messageService, channel, user);
    }
    static User setupUser(UserService userService) {
        User user = userService.createUser("woody", "woody1234", 25, "woody@codeit.com");
        return user;
    }

//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.createChannel("공지", "공지 채널입니다.");
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.createMessage("안녕하세요. 저는 우디입니다", channel.getId(), author.getId());
//        System.out.println("메시지 생성: " + message.getId());
//    }
    public void FileCRUDTest() {
        System.out.println("-----------------FileCRUDTest-----------------");

        // 등록

        User user1 = setupUser(userService);

        // 조회(단건)
        System.out.println("-----------------조회(단건)Test-----------------");
        System.out.println("User: " + userService.foundUser(user1.getId()));

    }
}
